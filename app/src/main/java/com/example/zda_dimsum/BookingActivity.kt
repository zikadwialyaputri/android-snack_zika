package com.example.zda_dimsum

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var btnTanggal: Button
    private lateinit var tvSummaryDate: TextView
    private lateinit var tvSummaryTime: TextView

    private lateinit var btnJam08: Button
    private lateinit var btnJam10: Button
    private lateinit var btnJam13: Button
    private lateinit var btnJam15: Button
    private lateinit var btnJam17: Button
    private lateinit var btnJam19: Button
    private lateinit var btnJam21: Button

    private var selectedTime: String = ""
    private var selectedDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        // Izin notifikasi Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbarBooking)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val btnBack = findViewById<ImageButton>(R.id.btnBackBooking)

        btnBack.setOnClickListener {
            finish()
        }

        btnTanggal = findViewById(R.id.btnTanggal)
        tvSummaryDate = findViewById(R.id.tvSummaryDate)
        tvSummaryTime = findViewById(R.id.tvSummaryTime)

        btnJam08 = findViewById(R.id.btnJam08)
        btnJam10 = findViewById(R.id.btnJam10)
        btnJam13 = findViewById(R.id.btnJam13)
        btnJam15 = findViewById(R.id.btnJam15)
        btnJam17 = findViewById(R.id.btnJam17)
        btnJam19 = findViewById(R.id.btnJam19)
        btnJam21 = findViewById(R.id.btnJam21)

        btnTanggal.setOnClickListener {
            showDatePicker()
        }

        btnJam08.setOnClickListener {
            selectTime("08.00", btnJam08)
        }

        btnJam10.setOnClickListener {
            selectTime("10.00", btnJam10)
        }

        btnJam13.setOnClickListener {
            selectTime("13.00", btnJam13)
        }

        btnJam15.setOnClickListener {
            selectTime("15.00", btnJam15)
        }

        btnJam17.setOnClickListener {
            selectTime("17.00", btnJam17)
        }

        btnJam19.setOnClickListener {
            selectTime("19.00", btnJam19)
        }

        btnJam21.setOnClickListener {
            selectTime("21.00", btnJam21)
        }

        // Reset tampilan jam supaya belum ada yang terlihat terpilih
        resetTimeButtons()

        val btnKonfirmasi = findViewById<Button>(R.id.btnKonfirmasi)

        btnKonfirmasi.setOnClickListener {

            if (selectedDate == null) {
                Toast.makeText(
                    this,
                    "Silakan pilih tanggal terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (selectedTime.isEmpty()) {
                Toast.makeText(
                    this,
                    "Silakan pilih jam terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val reminderBerhasil = scheduleBookingReminder()

            if (reminderBerhasil) {
                Toast.makeText(
                    this,
                    "Booking berhasil! Notifikasi akan muncul sekitar 10 detik lagi.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Gagal membuat pengingat booking.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val selectedCalendar = Calendar.getInstance()

                selectedCalendar.set(
                    selectedYear,
                    selectedMonth,
                    selectedDay,
                    0,
                    0,
                    0
                )

                selectedCalendar.set(Calendar.MILLISECOND, 0)

                selectedDate = selectedCalendar

                val dateFormat = SimpleDateFormat(
                    "dd MMMM yyyy",
                    Locale("id", "ID")
                )

                val formattedDate = dateFormat.format(selectedCalendar.time)

                btnTanggal.text = formattedDate
                tvSummaryDate.text = formattedDate

                // Tampilkan tanggal yang dipilih pada kartu summary
                findViewById<TextView>(R.id.tvBookingLokasi).text =
                    formattedDate
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun selectTime(time: String, selectedButton: Button) {

        selectedTime = time
        tvSummaryTime.text = "$time WIB"

        resetTimeButtons()

        selectedButton.backgroundTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    R.color.lapangin_green
                )
            )

        selectedButton.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.lapangin_white
            )
        )

        Toast.makeText(
            this,
            "Jam $time dipilih",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun resetTimeButtons() {

        val buttons = listOf(
            btnJam08,
            btnJam10,
            btnJam13,
            btnJam15,
            btnJam17,
            btnJam19,
            btnJam21
        )

        for (button in buttons) {

            button.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.lapangin_white
                    )
                )

            button.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.lapangin_green
                )
            )
        }
    }

    private fun scheduleBookingReminder(): Boolean {

        val bookingDate = selectedDate ?: return false

        if (selectedTime.isEmpty()) {
            return false
        }

        val timeParts = selectedTime.split(".")

        if (timeParts.size != 2) {
            return false
        }

        val hour = timeParts[0].toIntOrNull() ?: return false
        val minute = timeParts[1].toIntOrNull() ?: return false

        val bookingCalendar = Calendar.getInstance()

        bookingCalendar.set(
            bookingDate.get(Calendar.YEAR),
            bookingDate.get(Calendar.MONTH),
            bookingDate.get(Calendar.DAY_OF_MONTH),
            hour,
            minute,
            0
        )

        bookingCalendar.set(Calendar.MILLISECOND, 0)

        // Mode testing: notifikasi 10 detik setelah booking
        bookingCalendar.timeInMillis =
            System.currentTimeMillis() + 10000

        val intent = Intent(
            this,
            BookingReminderReceiver::class.java
        ).apply {
            putExtra("NAMA_LAPANGAN", "GOR WIRA")
            putExtra("JAM_BOOKING", selectedTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            bookingCalendar.timeInMillis,
            pendingIntent
        )

        return true
    }
}