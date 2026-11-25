
package com.example.zda_dimsum.booking

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.zda_dimsum.BookingReminderReceiver
import com.example.zda_dimsum.R
import com.example.zda_dimsum.data.api.SupabaseClient
import com.example.zda_dimsum.data.model.BookingModel
import com.example.zda_dimsum.data.model.LapanganModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingFragment : Fragment() {

    private lateinit var layoutDaftarLapangan: LinearLayout
    private lateinit var layoutFormBooking: LinearLayout
    private lateinit var containerDaftarLapangan: LinearLayout
    private lateinit var progressLapangan: ProgressBar
    private lateinit var tvKosongLapangan: TextView

    private lateinit var tvBookingTitle: TextView
    private lateinit var tvBookingLocation: TextView
    private lateinit var tvHargaLapangan: TextView
    private lateinit var btnPilihTanggal: TextView
    private lateinit var spinnerJam: Spinner
    private lateinit var tvBookingSummary: TextView
    private lateinit var tvTotalHarga: TextView
    private lateinit var btnLanjutBooking: Button

    private var daftarLapangan = listOf<LapanganModel>()
    private var lapanganTerpilih: LapanganModel? = null

    private var tanggalDatabase: String = ""
    private var tanggalTampil: String = ""

    private var pendingNamaLapangan = ""
    private var pendingJam = ""
    private var pendingWaktu: Calendar? = null

    private val formatDatabase = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    )

    private val formatTampil = SimpleDateFormat(
        "dd MMMM yyyy",
        Locale("id", "ID")
    )

    private val daftarJam = listOf(
        "14:00", "15:00", "16:00", "17:00",
        "18:00", "19:00", "20:00", "21:00"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutDaftarLapangan = view.findViewById(R.id.layoutDaftarLapangan)
        layoutFormBooking = view.findViewById(R.id.layoutFormBooking)
        containerDaftarLapangan = view.findViewById(R.id.containerDaftarLapangan)
        progressLapangan = view.findViewById(R.id.progressLapangan)
        tvKosongLapangan = view.findViewById(R.id.tvKosongLapangan)

        tvBookingTitle = view.findViewById(R.id.tvBookingTitle)
        tvBookingLocation = view.findViewById(R.id.tvBookingLocation)
        tvHargaLapangan = view.findViewById(R.id.tvHargaLapangan)
        btnPilihTanggal = view.findViewById(R.id.btnPilihTanggal)
        spinnerJam = view.findViewById(R.id.spinnerJam)
        tvBookingSummary = view.findViewById(R.id.tvBookingSummary)
        tvTotalHarga = view.findViewById(R.id.tvTotalHarga)
        btnLanjutBooking = view.findViewById(R.id.btnLanjutBooking)

        val adapterJam = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            daftarJam
        )
        adapterJam.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerJam.adapter = adapterJam

        view.findViewById<TextView>(R.id.btnKembaliLapangan)
            .setOnClickListener {
                tampilkanDaftarLapangan()
            }

        btnPilihTanggal.setOnClickListener {
            bukaDatePicker()
        }

        btnLanjutBooking.setOnClickListener {
            simpanBooking()
        }

        tampilkanDaftarLapangan()
        ambilLapangan()
    }

    private fun ambilLapangan() {
        progressLapangan.visibility = View.VISIBLE
        tvKosongLapangan.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                daftarLapangan = SupabaseClient.lapanganService
                    .getLapangan(SupabaseClient.API_KEY)

                progressLapangan.visibility = View.GONE
                tampilkanKartuLapangan()

            } catch (e: Exception) {
                progressLapangan.visibility = View.GONE
                tvKosongLapangan.visibility = View.VISIBLE
                tvKosongLapangan.text =
                    "Gagal mengambil data lapangan: ${e.message}"
            }
        }
    }

    private fun tampilkanKartuLapangan() {
        containerDaftarLapangan.removeAllViews()

        if (daftarLapangan.isEmpty()) {
            tvKosongLapangan.visibility = View.VISIBLE
            tvKosongLapangan.text = "Belum ada lapangan tersedia"
            return
        }

        tvKosongLapangan.visibility = View.GONE

        daftarLapangan.forEach { lapangan ->

            val kartu = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(20, 20, 20, 20)
                setBackgroundColor(
                    android.graphics.Color.WHITE
                )
                elevation = 4f
            }

            val nama = TextView(requireContext()).apply {
                text = lapangan.nama
                textSize = 18f
                setTextColor(
                    android.graphics.Color.rgb(23, 63, 50)
                )
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val lokasi = TextView(requireContext()).apply {
                text = lapangan.lokasi
                textSize = 14f
                setTextColor(
                    android.graphics.Color.DKGRAY
                )
                setPadding(0, 8, 0, 0)
            }

            val harga = TextView(requireContext()).apply {
                text = "Rp ${formatHarga(lapangan.harga)} / jam"
                textSize = 16f
                setTextColor(
                    android.graphics.Color.rgb(40, 118, 83)
                )
                setPadding(0, 10, 0, 12)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val tombol = Button(requireContext()).apply {
                text = "Pilih Lapangan"
                setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.rgb(40, 118, 83)
                    )
                )
                setTextColor(android.graphics.Color.WHITE)
                setOnClickListener {
                    pilihLapangan(lapangan)
                }
            }

            kartu.addView(nama)
            kartu.addView(lokasi)
            kartu.addView(harga)
            kartu.addView(tombol)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 20

            containerDaftarLapangan.addView(kartu, params)
        }
    }

    private fun pilihLapangan(lapangan: LapanganModel) {
        lapanganTerpilih = lapangan

        tvBookingTitle.text = lapangan.nama
        tvBookingLocation.text = lapangan.lokasi
        tvHargaLapangan.text =
            "Rp ${formatHarga(lapangan.harga)} / jam"

        tanggalDatabase = ""
        tanggalTampil = ""
        btnPilihTanggal.text = "Pilih tanggal booking"

        spinnerJam.setSelection(0)
        updateRingkasan()

        layoutDaftarLapangan.visibility = View.GONE
        layoutFormBooking.visibility = View.VISIBLE
    }

    private fun tampilkanDaftarLapangan() {
        layoutFormBooking.visibility = View.GONE
        layoutDaftarLapangan.visibility = View.VISIBLE
        lapanganTerpilih = null
    }

    private fun bukaDatePicker() {
        val kalender = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, tahun, bulan, hari ->

                val tanggal = Calendar.getInstance().apply {
                    set(tahun, bulan, hari)
                }

                val hariIni = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (tanggal.before(hariIni)) {
                    Toast.makeText(
                        requireContext(),
                        "Tanggal tidak boleh sudah lewat",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@DatePickerDialog
                }

                tanggalDatabase = formatDatabase.format(tanggal.time)
                tanggalTampil = formatTampil.format(tanggal.time)

                btnPilihTanggal.text = tanggalTampil
                updateRingkasan()

            },
            kalender.get(Calendar.YEAR),
            kalender.get(Calendar.MONTH),
            kalender.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    private fun updateRingkasan() {
        val lapangan = lapanganTerpilih ?: return
        val jam = spinnerJam.selectedItem?.toString() ?: "-"

        tvBookingSummary.text =
            "Lapangan: ${lapangan.nama}\n" +
                    "Tanggal: ${if (tanggalTampil.isBlank()) "-" else tanggalTampil}\n" +
                    "Jam: $jam"

        tvTotalHarga.text =
            "Total: Rp ${formatHarga(lapangan.harga)}"
    }

    private fun simpanBooking() {
        val lapangan = lapanganTerpilih

        if (lapangan == null) {
            Toast.makeText(
                requireContext(),
                "Pilih lapangan terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (tanggalDatabase.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Pilih tanggal booking terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val jam = spinnerJam.selectedItem.toString()

        val preferences = requireContext()
            .getSharedPreferences("user_pref", Context.MODE_PRIVATE)

        val userId = preferences.getString("user_id", null)

        if (userId.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "Silakan login kembali",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val waktuBooking = Calendar.getInstance()
        val bagianJam = jam.split(":")
        waktuBooking.set(
            tanggalDatabase.substring(0, 4).toInt(),
            tanggalDatabase.substring(5, 7).toInt() - 1,
            tanggalDatabase.substring(8, 10).toInt(),
            bagianJam[0].toInt(),
            bagianJam[1].toInt(),
            0
        )

        if (waktuBooking.timeInMillis <= System.currentTimeMillis()) {
            Toast.makeText(
                requireContext(),
                "Jam booking sudah terlewati",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dataBooking = BookingModel(
            userId = userId,
            lapanganId = lapangan.id
                ?: throw IllegalStateException("ID lapangan tidak ditemukan"),
            tanggal = tanggalDatabase,
            jam = jam,
            totalHarga = lapangan.harga,
            status = "Menunggu"
        )

        btnLanjutBooking.isEnabled = false
        btnLanjutBooking.text = "Menyimpan..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = SupabaseClient.bookingService
                    .tambahBooking(
                        SupabaseClient.API_KEY,
                        dataBooking
                    )

                if (!response.isSuccessful) {
                    throw Exception(
                        response.errorBody()?.string()
                            ?: "Gagal menyimpan booking"
                    )
                }

                pendingNamaLapangan = lapangan.nama
                pendingJam = jam
                pendingWaktu = waktuBooking

                AlertDialog.Builder(requireContext())
                    .setTitle("Booking Berhasil!")
                    .setMessage(
                        "Booking lapangan ${lapangan.nama} berhasil disimpan.\n\n" +
                                "Status: Menunggu\n" +
                                "Tanggal: $tanggalTampil\n" +
                                "Jam: $jam"
                    )
                    .setCancelable(false)
                    .setPositiveButton("Lihat Riwayat") { _, _ ->
                        jadwalkanPengingat()
                        bukaTabRiwayat()
                    }
                    .show()

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Booking gagal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnLanjutBooking.isEnabled = true
                btnLanjutBooking.text = "Lanjut Booking"
            }
        }
    }

    private fun jadwalkanPengingat() {
        val waktu = pendingWaktu ?: return

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                200
            )
            return
        }

        val intent = Intent(
            requireContext(),
            BookingReminderReceiver::class.java
        ).apply {
            putExtra("NAMA_LAPANGAN", pendingNamaLapangan)
            putExtra("JAM_BOOKING", pendingJam)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            waktu.timeInMillis.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            waktu.timeInMillis,
            pendingIntent
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 200 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            jadwalkanPengingat()
        }
    }

    private fun bukaTabRiwayat() {
        val activity = activity ?: return

        // Mencari BottomNavigationView yang digunakan oleh BaseActivity.
        val bottomNavigation = cariBottomNavigation(activity.window.decorView)

        if (bottomNavigation != null) {
            val menuItem = (0 until bottomNavigation.menu.size())
                .map { bottomNavigation.menu.getItem(it) }
                .firstOrNull {
                    it.title.toString().contains(
                        "riwayat",
                        ignoreCase = true
                    )
                }

            if (menuItem != null) {
                bottomNavigation.selectedItemId = menuItem.itemId
                return
            }
        }

        Toast.makeText(
            requireContext(),
            "Booking tersimpan. Silakan buka tab Riwayat.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun cariBottomNavigation(view: View): com.google.android.material.bottomnavigation.BottomNavigationView? {
        if (view is com.google.android.material.bottomnavigation.BottomNavigationView) {
            return view
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val hasil = cariBottomNavigation(view.getChildAt(i))
                if (hasil != null) return hasil
            }
        }

        return null
    }

    private fun formatHarga(harga: Double): String {
        return NumberFormat.getNumberInstance(
            Locale("id", "ID")
        ).format(harga)
    }
}
