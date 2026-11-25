
package com.example.zda_dimsum.home

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zda_dimsum.R
import com.example.zda_dimsum.data.api.SupabaseClient
import com.example.zda_dimsum.data.model.LapanganModel
import com.example.zda_dimsum.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val daftarLapangan = mutableListOf<LapanganModel>()
    private lateinit var adapter: LapanganAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupPencarian()
        setupFasilitas()
        setupLokasi()
        setupTanggal()

        binding.btnTambahLapangan.setOnClickListener {
            tampilkanFormLapangan()
        }

        ambilDataLapangan()
    }

    // READ: mengambil data lapangan dari Supabase
    private fun ambilDataLapangan() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val hasil = SupabaseClient.lapanganService.getLapangan(
                    apiKey = SupabaseClient.API_KEY
                )

                daftarLapangan.clear()
                daftarLapangan.addAll(hasil)

                tampilkanHasilPencarian(
                    binding.etSearch.text.toString()
                )

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal mengambil data: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = LapanganAdapter(daftarLapangan) { lapangan ->
            tampilkanMenuLapangan(lapangan)
        }

        binding.rvLapangan.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvLapangan.adapter = adapter
        binding.rvLapangan.isNestedScrollingEnabled = false
    }

    // SEARCH
    private fun setupPencarian() {
        binding.etSearch.doAfterTextChanged { teks ->
            tampilkanHasilPencarian(teks.toString())
        }
    }

    private fun tampilkanHasilPencarian(kataKunci: String) {
        val hasil = daftarLapangan.filter {
            it.nama.contains(kataKunci, true) ||
                    it.lokasi.contains(kataKunci, true)
        }

        adapter.updateData(hasil)
    }

    // MENU EDIT DAN DELETE
    private fun tampilkanMenuLapangan(
        lapangan: LapanganModel
    ) {
        val pilihan = arrayOf(
            "Edit Lapangan",
            "Hapus Lapangan",
            "Batal"
        )

        AlertDialog.Builder(requireContext())
            .setTitle(lapangan.nama)
            .setItems(pilihan) { dialog, posisi ->
                when (posisi) {
                    0 -> tampilkanFormLapangan(lapangan)
                    1 -> konfirmasiHapus(lapangan)
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    // FORM CREATE DAN UPDATE
    private fun tampilkanFormLapangan(
        lapangan: LapanganModel? = null
    ) {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 8)
        }

        fun buatInput(
            hintInput: String,
            nilaiAwal: String = ""
        ): EditText {
            return EditText(requireContext()).apply {
                hint = hintInput
                setText(nilaiAwal)
                textSize = 14f
                isSingleLine = true

                layout.addView(
                    this,
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }

        val etNama = buatInput(
            "Nama lapangan",
            lapangan?.nama.orEmpty()
        )

        val etLokasi = buatInput(
            "Lokasi lapangan",
            lapangan?.lokasi.orEmpty()
        )

        val etHarga = buatInput(
            "Harga per jam",
            lapangan?.harga?.toString().orEmpty()
        ).apply {
            inputType =
                android.text.InputType.TYPE_CLASS_NUMBER or
                        android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val etDeskripsi = buatInput(
            "Deskripsi",
            lapangan?.deskripsi.orEmpty()
        )

        val etGambar = buatInput(
            "URL gambar (opsional)",
            lapangan?.gambar.orEmpty()
        )

        val judul = if (lapangan == null) {
            "Tambah Lapangan"
        } else {
            "Edit Lapangan"
        }

        AlertDialog.Builder(requireContext())
            .setTitle(judul)
            .setView(layout)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", null)
            .create()
            .also { dialog ->
                dialog.setOnShowListener {
                    dialog.getButton(
                        AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener {

                        val nama = etNama.text.toString().trim()
                        val lokasi = etLokasi.text.toString().trim()
                        val harga = etHarga.text.toString()
                            .trim().toDoubleOrNull()
                        val deskripsi =
                            etDeskripsi.text.toString().trim()
                        val gambar =
                            etGambar.text.toString().trim()

                        if (nama.isEmpty() || lokasi.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Nama dan lokasi wajib diisi",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        if (harga == null || harga <= 0) {
                            Toast.makeText(
                                requireContext(),
                                "Masukkan harga yang valid",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        val data = LapanganModel(
                            id = lapangan?.id,
                            nama = nama,
                            lokasi = lokasi,
                            harga = harga,
                            deskripsi = deskripsi.ifEmpty { null },
                            gambar = gambar.ifEmpty { null }
                        )

                        if (lapangan == null) {
                            tambahLapangan(data, dialog)
                        } else {
                            ubahLapangan(data, dialog)
                        }
                    }
                }

                dialog.show()
            }
    }

    // CREATE
    private fun tambahLapangan(
        data: LapanganModel,
        dialog: AlertDialog
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response =
                    SupabaseClient.lapanganService.tambahLapangan(
                        apiKey = SupabaseClient.API_KEY,
                        data = data
                    )

                if (response.isSuccessful) {
                    dialog.dismiss()

                    Toast.makeText(
                        requireContext(),
                        "Lapangan berhasil ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()

                    ambilDataLapangan()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Gagal menambah: ${
                            response.errorBody()?.string()
                        }",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // UPDATE
    private fun ubahLapangan(
        data: LapanganModel,
        dialog: AlertDialog
    ) {
        val id = data.id ?: return

        val perubahan = mapOf(
            "nama" to data.nama,
            "lokasi" to data.lokasi,
            "harga" to data.harga,
            "deskripsi" to data.deskripsi,
            "gambar" to data.gambar
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response =
                    SupabaseClient.lapanganService.ubahLapangan(
                        apiKey = SupabaseClient.API_KEY,
                        id = "eq.$id",
                        data = perubahan
                    )

                if (response.isSuccessful) {
                    dialog.dismiss()

                    Toast.makeText(
                        requireContext(),
                        "Data lapangan berhasil diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()

                    ambilDataLapangan()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengubah: ${
                            response.errorBody()?.string()
                        }",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // KONFIRMASI DELETE
    private fun konfirmasiHapus(
        lapangan: LapanganModel
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Lapangan")
            .setMessage(
                "Apakah kamu yakin ingin menghapus ${lapangan.nama}?"
            )
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                hapusLapangan(lapangan)
            }
            .show()
    }

    // DELETE
    private fun hapusLapangan(
        lapangan: LapanganModel
    ) {
        val id = lapangan.id ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response =
                    SupabaseClient.lapanganService.hapusLapangan(
                        apiKey = SupabaseClient.API_KEY,
                        id = "eq.$id"
                    )

                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Lapangan berhasil dihapus",
                        Toast.LENGTH_SHORT
                    ).show()

                    ambilDataLapangan()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Gagal menghapus: ${
                            response.errorBody()?.string()
                        }",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // KATEGORI
    private fun setupFasilitas() {
        binding.tvIndoor.setOnClickListener {
            tampilkanPesan("Lapangan Indoor")
        }

        binding.tvOutdoor.setOnClickListener {
            tampilkanPesan("Lapangan Outdoor")
        }

        binding.tvParkir.setOnClickListener {
            tampilkanPesan("Area Parkir")
        }

        binding.tvRuangGanti.setOnClickListener {
            tampilkanPesan("Ruang Ganti")
        }
    }

    // LOKASI
    private fun setupLokasi() {
        binding.cardWira.setOnClickListener {
            tampilkanPesan("GOR WIRA")
        }

        binding.cardDinda.setOnClickListener {
            tampilkanPesan("GOR DINDA")
        }

        binding.cardLele.setOnClickListener {
            tampilkanPesan("GOR LELE")
        }
    }

    // TANGGAL
    private fun setupTanggal() {
        val tanggalViews = listOf(
            binding.tanggal1,
            binding.tanggal2,
            binding.tanggal3,
            binding.tanggal4,
            binding.tanggal5,
            binding.tanggal6
        )

        val kalender = Calendar.getInstance()

        val formatHari = SimpleDateFormat(
            "EEE",
            Locale("id", "ID")
        )

        val formatTanggal = SimpleDateFormat(
            "dd",
            Locale("id", "ID")
        )

        tanggalViews.forEachIndexed { index, textView ->
            val tanggal = kalender.clone() as Calendar
            tanggal.add(Calendar.DAY_OF_MONTH, index)

            textView.text =
                "${formatHari.format(tanggal.time)}\n" +
                        formatTanggal.format(tanggal.time)

            textView.setOnClickListener {
                pilihTanggal(tanggalViews, textView)

                tampilkanPesan(
                    SimpleDateFormat(
                        "dd MMMM yyyy",
                        Locale("id", "ID")
                    ).format(tanggal.time)
                )
            }
        }

        pilihTanggal(tanggalViews, tanggalViews[0])
    }

    private fun pilihTanggal(
        semuaTanggal: List<TextView>,
        terpilih: TextView
    ) {
        semuaTanggal.forEach { tanggal ->
            tanggal.setBackgroundResource(
                if (tanggal == terpilih) {
                    R.drawable.bg_date_selected
                } else {
                    R.drawable.bg_date_normal
                }
            )

            tanggal.setTextColor(
                if (tanggal == terpilih) {
                    Color.WHITE
                } else {
                    Color.rgb(35, 139, 69)
                }
            )
        }
    }

    private fun tampilkanPesan(pesan: String) {
        Toast.makeText(
            requireContext(),
            pesan,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        binding.rvLapangan.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
