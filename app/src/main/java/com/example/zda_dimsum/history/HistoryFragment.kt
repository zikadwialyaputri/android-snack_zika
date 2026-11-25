
package com.example.zda_dimsum.history

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.zda_dimsum.R
import com.example.zda_dimsum.data.api.SupabaseClient
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryFragment : Fragment() {

    private lateinit var listRiwayat: ListView
    private lateinit var filterSemua: TextView
    private lateinit var filterAkanDatang: TextView
    private lateinit var filterSelesai: TextView

    private var semuaRiwayat = listOf<RiwayatModel>()
    private var filterAktif = "Semua"

    private val formatTanggalDatabase =
        SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val formatTanggalTampil =
        SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    private val batasEditMillis = 60 * 60 * 1000L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_history,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        listRiwayat = view.findViewById(R.id.listRiwayat)
        filterSemua = view.findViewById(R.id.filterSemua)
        filterAkanDatang = view.findViewById(R.id.filterAkanDatang)
        filterSelesai = view.findViewById(R.id.filterSelesai)

        filterSemua.setOnClickListener {
            ubahFilter("Semua")
        }

        filterAkanDatang.setOnClickListener {
            ubahFilter("Menunggu")
        }

        filterSelesai.setOnClickListener {
            ubahFilter("Selesai")
        }

        ubahFilter("Semua")
    }

    override fun onResume() {
        super.onResume()

        if (::listRiwayat.isInitialized) {
            ambilRiwayat()
        }
    }

    private fun ambilUserId(): String? {
        return requireContext()
            .getSharedPreferences(
                "user_pref",
                android.content.Context.MODE_PRIVATE
            )
            .getString("user_id", null)
    }

    private fun ambilRiwayat() {

        val userId = ambilUserId()

        if (userId.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "Data pengguna tidak ditemukan. Silakan login kembali.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val bookings = SupabaseClient.bookingService
                    .getBooking(SupabaseClient.API_KEY)

                val lapangan = SupabaseClient.lapanganService
                    .getLapangan(SupabaseClient.API_KEY)

                semuaRiwayat = bookings
                    .filter { it.userId == userId }
                    .mapNotNull { booking ->

                        val dataLapangan = lapangan.firstOrNull {
                            it.id == booking.lapanganId
                        } ?: return@mapNotNull null

                        val tanggal = try {
                            val date = formatTanggalDatabase.parse(
                                booking.tanggal
                            )

                            if (date != null) {
                                formatTanggalTampil.format(date)
                            } else {
                                booking.tanggal
                            }
                        } catch (e: Exception) {
                            booking.tanggal
                        }

                        val jamMulai = booking.jam.take(5)

                        val jamSelesai = try {
                            val parts = jamMulai.split(":")
                            val kalender = Calendar.getInstance()

                            kalender.set(
                                Calendar.HOUR_OF_DAY,
                                parts[0].toInt()
                            )

                            kalender.set(
                                Calendar.MINUTE,
                                parts[1].toInt()
                            )

                            kalender.set(Calendar.SECOND, 0)
                            kalender.add(Calendar.HOUR_OF_DAY, 1)

                            SimpleDateFormat(
                                "HH:mm",
                                Locale.getDefault()
                            ).format(kalender.time)

                        } catch (e: Exception) {
                            "-"
                        }

                        val harga = NumberFormat.getNumberInstance(
                            Locale("id", "ID")
                        ).format(booking.totalHarga)

                        RiwayatModel(
                            id = booking.id ?: 0L,
                            namaLapangan = dataLapangan.nama,
                            lokasi = dataLapangan.lokasi,
                            tanggal = tanggal,
                            jam = "$jamMulai - $jamSelesai",
                            harga = "Rp $harga",
                            status = booking.status,
                            tanggalDatabase = booking.tanggal,
                            jamDatabase = booking.jam
                        )
                    }

                tampilkanRiwayat()

            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengambil riwayat: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun tampilkanRiwayat() {

        val dataTampil = when (filterAktif) {
            "Menunggu" -> semuaRiwayat.filter {
                it.status == "Menunggu"
            }

            "Selesai" -> semuaRiwayat.filter {
                it.status == "Selesai"
            }

            else -> semuaRiwayat
        }

        listRiwayat.adapter = RiwayatAdapter(
            requireContext(),
            dataTampil
        ) { item ->
            tampilkanDetail(item)
        }
    }

    private fun ubahFilter(filter: String) {

        filterAktif = filter

        filterSemua.alpha =
            if (filter == "Semua") 1f else 0.5f

        filterAkanDatang.alpha =
            if (filter == "Menunggu") 1f else 0.5f

        filterSelesai.alpha =
            if (filter == "Selesai") 1f else 0.5f

        if (::listRiwayat.isInitialized) {
            tampilkanRiwayat()
        }
    }

    private fun ambilWaktuBooking(item: RiwayatModel): Long? {

        return try {
            val jam = item.jamDatabase.take(5)

            val formatter = SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.US
            )

            formatter.isLenient = false

            formatter.parse(
                "${item.tanggalDatabase} $jam"
            )?.time

        } catch (e: Exception) {
            null
        }
    }

    private fun bolehDikelola(item: RiwayatModel): Boolean {

        if (item.status != "Menunggu") {
            return false
        }

        val waktuBooking = ambilWaktuBooking(item)
            ?: return false

        val sisaWaktu = waktuBooking - System.currentTimeMillis()

        return sisaWaktu > batasEditMillis
    }

    private fun tampilkanDetail(item: RiwayatModel) {

        val masihBisaDiubah = bolehDikelola(item)

        val informasi =
            "Lapangan: ${item.namaLapangan}\n" +
                    "Lokasi: ${item.lokasi}\n" +
                    "Tanggal: ${item.tanggal}\n" +
                    "Jam: ${item.jam}\n" +
                    "Harga: ${item.harga}\n" +
                    "Status: ${item.status}\n\n" +
                    if (masihBisaDiubah) {
                        "Kamu masih bisa mengedit jam atau menghapus booking."
                    } else {
                        "Booking tidak bisa diedit atau dihapus. " +
                                "Perubahan hanya diperbolehkan lebih dari 1 jam sebelum mulai."
                    }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Detail Booking")
            .setMessage(informasi)
            .setNegativeButton("Tutup", null)

        if (masihBisaDiubah) {

            dialog.setPositiveButton("Edit Jam") { _, _ ->
                editJamBooking(item)
            }

            dialog.setNeutralButton("Hapus Booking") { _, _ ->
                konfirmasiHapus(item)
            }
        }

        dialog.show()
    }

    private fun editJamBooking(item: RiwayatModel) {

        val bagianJam = item.jamDatabase
            .take(5)
            .split(":")

        val jamAwal = bagianJam[0].toIntOrNull() ?: 14
        val menitAwal = bagianJam[1].toIntOrNull() ?: 0

        TimePickerDialog(
            requireContext(),
            { _, jamBaru, menitBaru ->

                val jamFormat = String.format(
                    Locale.US,
                    "%02d:%02d",
                    jamBaru,
                    menitBaru
                )

                val waktuLama = ambilWaktuBooking(item)
                val waktuSekarang = System.currentTimeMillis()

                if (
                    waktuLama == null ||
                    waktuLama - waktuSekarang <= batasEditMillis
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Booking sudah memasuki batas 1 jam sebelum mulai.",
                        Toast.LENGTH_LONG
                    ).show()

                    ambilRiwayat()
                    return@TimePickerDialog
                }

                val formatter = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm",
                    Locale.US
                )

                formatter.isLenient = false

                val waktuBaru = try {
                    formatter.parse(
                        "${item.tanggalDatabase} $jamFormat"
                    )?.time
                } catch (e: Exception) {
                    null
                }

                if (
                    waktuBaru == null ||
                    waktuBaru - waktuSekarang <= batasEditMillis
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Jam baru harus lebih dari 1 jam dari sekarang.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@TimePickerDialog
                }

                simpanPerubahanJam(item, jamFormat)

            },
            jamAwal,
            menitAwal,
            true
        ).show()
    }

    private fun simpanPerubahanJam(
        item: RiwayatModel,
        jamBaru: String
    ) {

        val userId = ambilUserId()

        if (userId.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "Pengguna tidak ditemukan.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                if (!bolehDikelola(item)) {
                    Toast.makeText(
                        requireContext(),
                        "Booking sudah tidak bisa diubah.",
                        Toast.LENGTH_LONG
                    ).show()

                    ambilRiwayat()
                    return@launch
                }

                val response = SupabaseClient.bookingService.ubahBooking(
                    SupabaseClient.API_KEY,
                    "eq.${item.id}",
                    "eq.$userId",
                    mapOf("jam" to jamBaru)
                )

                if (!response.isSuccessful) {
                    throw Exception(
                        response.errorBody()?.string()
                            ?: "Gagal mengubah jam booking"
                    )
                }

                if (response.body().isNullOrEmpty()) {
                    throw Exception(
                        "Data booking tidak ditemukan atau tidak memiliki izin untuk diubah."
                    )
                }

                Toast.makeText(
                    requireContext(),
                    "Jam booking berhasil diperbarui!",
                    Toast.LENGTH_SHORT
                ).show()

                ambilRiwayat()

            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengubah jam: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun konfirmasiHapus(item: RiwayatModel) {

        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Booking?")
            .setMessage(
                "Apakah kamu yakin ingin menghapus booking " +
                        "${item.namaLapangan} pada tanggal ${item.tanggal}?"
            )
            .setNegativeButton("Tidak", null)
            .setPositiveButton("Ya, Hapus") { _, _ ->
                hapusBooking(item)
            }
            .show()
    }

    private fun hapusBooking(item: RiwayatModel) {

        val userId = ambilUserId()

        if (userId.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "Pengguna tidak ditemukan.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                if (!bolehDikelola(item)) {
                    Toast.makeText(
                        requireContext(),
                        "Booking sudah tidak bisa dihapus.",
                        Toast.LENGTH_LONG
                    ).show()

                    ambilRiwayat()
                    return@launch
                }

                val response = SupabaseClient.bookingService.hapusBooking(
                    SupabaseClient.API_KEY,
                    "eq.${item.id}",
                    "eq.$userId"
                )

                if (!response.isSuccessful) {
                    throw Exception(
                        response.errorBody()?.string()
                            ?: "Gagal menghapus booking"
                    )
                }

                if (response.body().isNullOrEmpty()) {
                    throw Exception(
                        "Booking tidak ditemukan atau tidak memiliki izin untuk dihapus."
                    )
                }

                Toast.makeText(
                    requireContext(),
                    "Booking berhasil dihapus!",
                    Toast.LENGTH_SHORT
                ).show()

                ambilRiwayat()

            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal menghapus booking: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
