package com.example.zda_dimsum.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zda_dimsum.R
import com.example.zda_dimsum.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val daftarLapangan = listOf(
        LapanganModel(
            "GOR WIRA",
            "Jl. Suka, Pekanbaru, Riau",
            "Rp 60.000 / jam",
            R.drawable.gor_wira
        ),
        LapanganModel(
            "GOR DINDA",
            "Jl. Melur, Pekanbaru, Riau",
            "Rp 60.000 / jam",
            R.drawable.gor_wira
        ),
        LapanganModel(
            "GOR LELE",
            "Jl. Karya, Pekanbaru, Riau",
            "Rp 60.000 / jam",
            R.drawable.gor_wira
        )
    )

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

        setupRecyclerView(daftarLapangan)
        setupPencarian()
        setupFasilitas()
        setupLokasi()
        setupTanggal()
    }

    private fun setupRecyclerView(
        data: List<LapanganModel>
    ) {
        binding.rvLapangan.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvLapangan.adapter =
            LapanganAdapter(data) { lapangan ->
                tampilkanPesan(lapangan.nama)
            }

        binding.rvLapangan.isNestedScrollingEnabled = false
    }

    private fun setupPencarian() {
        binding.etSearch.doAfterTextChanged { teks ->

            val kataKunci = teks.toString().trim()

            val hasil = daftarLapangan.filter {
                it.nama.contains(kataKunci, true) ||
                        it.lokasi.contains(kataKunci, true)
            }

            setupRecyclerView(hasil)
        }
    }

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
            "EEE", Locale("id", "ID")
        )
        val formatTanggal = SimpleDateFormat(
            "dd", Locale("id", "ID")
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
                if (tanggal == terpilih)
                    R.drawable.bg_date_selected
                else
                    R.drawable.bg_date_normal
            )

            tanggal.setTextColor(
                if (tanggal == terpilih)
                    Color.WHITE
                else
                    Color.rgb(35, 139, 69)
            )
        }
    }

    private fun tampilkanPesan(nama: String) {
        Toast.makeText(
            requireContext(),
            "Kamu memilih $nama",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        binding.rvLapangan.adapter = null
        _binding = null
        super.onDestroyView()
    }
}