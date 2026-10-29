
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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zda_dimsum.BookingReminderReceiver
import com.example.zda_dimsum.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingFragment : Fragment() {

    private var selectedDate: Calendar? = null
    private var selectedTime: String = ""

    private lateinit var dateViews: List<TextView>
    private lateinit var timeViews: List<TextView>
    private lateinit var tvBookingSummary: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_booking,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvBookingSummary =
            view.findViewById(R.id.tvBookingSummary)

        val dateSen = view.findViewById<TextView>(R.id.dateSen)
        val dateSel = view.findViewById<TextView>(R.id.dateSel)
        val dateRab = view.findViewById<TextView>(R.id.dateRab)
        val dateKam = view.findViewById<TextView>(R.id.dateKam)
        val dateJum = view.findViewById<TextView>(R.id.dateJum)
        val dateSab = view.findViewById<TextView>(R.id.dateSab)

        dateViews = listOf(
            dateSen, dateSel, dateRab,
            dateKam, dateJum, dateSab
        )

        val time1400 = view.findViewById<TextView>(R.id.time1400)
        val time1500 = view.findViewById<TextView>(R.id.time1500)
        val time1600 = view.findViewById<TextView>(R.id.time1600)
        val time1700 = view.findViewById<TextView>(R.id.time1700)
        val time1800 = view.findViewById<TextView>(R.id.time1800)
        val time1900 = view.findViewById<TextView>(R.id.time1900)
        val time2000 = view.findViewById<TextView>(R.id.time2000)
        val time2100 = view.findViewById<TextView>(R.id.time2100)

        timeViews = listOf(
            time1400, time1500, time1600, time1700,
            time1800, time1900, time2000, time2100
        )

        setupDateChoices()

        dateViews.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                selectDate(index, textView)
            }
        }

        timeViews.forEach { textView ->
            textView.setOnClickListener {
                selectTime(textView.text.toString(), textView)
            }
        }

        val tvLihatKalender =
            view.findViewById<TextView>(R.id.tvLihatKalender)

        tvLihatKalender.setOnClickListener {
            showDatePicker()
        }

        val btnLanjutBooking =
            view.findViewById<TextView>(R.id.btnLanjutBooking)

        btnLanjutBooking.isEnabled = true
        btnLanjutBooking.isClickable = true

        btnLanjutBooking.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "TOMBOL BOOKING BERHASIL DIKLIK!",
                Toast.LENGTH_SHORT
            ).show()

            if (selectedDate == null) {
                Toast.makeText(
                    requireContext(),
                    "Pilih tanggal terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (selectedTime.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Pilih jam terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!requestNotificationPermissionIfNeeded()) {
                return@setOnClickListener
            }

            scheduleBookingReminder()
        }
    }

    private fun setupDateChoices() {
        val formatHari = SimpleDateFormat(
            "EEE", Locale("id", "ID")
        )
        val formatTanggal = SimpleDateFormat(
            "dd", Locale("id", "ID")
        )

        dateViews.forEachIndexed { index, textView ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, index)

            val namaHari = formatHari.format(calendar.time)
                .replace(".", "")
                .replaceFirstChar { it.uppercase() }

            val nomorTanggal =
                formatTanggal.format(calendar.time)

            textView.text = "$namaHari\n$nomorTanggal"
        }

        resetDateViews()
        updateBookingSummary()
    }

    private fun selectDate(index: Int, selectedView: TextView) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, index)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        selectedDate = calendar

        resetDateViews()

        selectedView.setBackgroundResource(
            R.drawable.bg_date_selected
        )
        selectedView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            )
        )

        updateBookingSummary()
    }

    private fun selectTime(time: String, selectedView: TextView) {
        selectedTime = time

        resetTimeViews()

        selectedView.setBackgroundResource(
            R.drawable.bg_time_selected
        )
        selectedView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            )
        )

        updateBookingSummary()
    }

    private fun resetDateViews() {
        dateViews.forEach { textView ->
            textView.setBackgroundResource(
                R.drawable.bg_date_normal
            )
            textView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.lapangin_green
                )
            )
        }
    }

    private fun resetTimeViews() {
        timeViews.forEach { textView ->
            textView.setBackgroundResource(
                R.drawable.bg_time_normal
            )
            textView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.lapangin_green
                )
            )
        }
    }

    private fun showDatePicker() {
        val today = Calendar.getInstance()

        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val calendar = Calendar.getInstance()
                calendar.set(
                    year, month, day, 0, 0, 0
                )
                calendar.set(Calendar.MILLISECOND, 0)

                selectedDate = calendar

                // Tanggal dari kalender bisa di luar enam pilihan.
                resetDateViews()
                updateBookingSummary()
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.minDate = today.timeInMillis
        dialog.show()
    }

    private fun updateBookingSummary() {
        val date = selectedDate

        if (date == null && selectedTime.isEmpty()) {
            tvBookingSummary.text = "Pilih tanggal dan jam"
            return
        }

        if (date == null) {
            tvBookingSummary.text = "Pilih tanggal • $selectedTime"
            return
        }

        val dateFormat = SimpleDateFormat(
            "EEEE, dd MMMM",
            Locale("id", "ID")
        )

        val formattedDate = dateFormat.format(date.time)

        tvBookingSummary.text = if (selectedTime.isEmpty()) {
            "$formattedDate • Pilih jam"
        } else {
            "$formattedDate • $selectedTime"
        }
    }

    private fun requestNotificationPermissionIfNeeded(): Boolean {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
            return false
        }

        return true
    }

    private fun scheduleBookingReminder() {
        val date = selectedDate ?: return

        val timeParts = selectedTime.split(":")
        if (timeParts.size != 2) {
            Toast.makeText(
                requireContext(),
                "Format jam booking tidak valid",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val bookingTime = date.clone() as Calendar
        bookingTime.set(
            Calendar.HOUR_OF_DAY,
            timeParts[0].toInt()
        )
        bookingTime.set(
            Calendar.MINUTE,
            timeParts[1].toInt()
        )
        bookingTime.set(Calendar.SECOND, 0)
        bookingTime.set(Calendar.MILLISECOND, 0)

        if (bookingTime.timeInMillis <= System.currentTimeMillis()) {
            Toast.makeText(
                requireContext(),
                "Tanggal atau jam booking sudah lewat",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val intent = Intent(
            requireContext(),
            BookingReminderReceiver::class.java
        ).apply {
            putExtra("NAMA_LAPANGAN", "GOR WIRA")
            putExtra("JAM_BOOKING", selectedTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Tes: pengingat muncul 10 detik setelah tombol ditekan.
        val testTriggerTime = System.currentTimeMillis() + 10_000L

        try {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                testTriggerTime,
                pendingIntent
            )

            Toast.makeText(
                requireContext(),
                "Booking diproses! Pengingat muncul sekitar 10 detik lagi.",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: SecurityException) {
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !alarmManager.canScheduleExactAlarms()
            ) {
                val settingsIntent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                )
                startActivity(settingsIntent)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Alarm gagal dijadwalkan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
