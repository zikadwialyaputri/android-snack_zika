package com.example.zda_dimsum

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var btnTanggal: Button

    private lateinit var tvSummaryDate: TextView
    private lateinit var tvSummaryTime: TextView

    private var selectedTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_booking)
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

        btnTanggal.setOnClickListener {
            showDatePicker()
        }
        val btnJam08 = findViewById<Button>(R.id.btnJam08)
        val btnJam10 = findViewById<Button>(R.id.btnJam10)
        val btnJam13 = findViewById<Button>(R.id.btnJam13)
        val btnJam15 = findViewById<Button>(R.id.btnJam15)
        val btnJam17 = findViewById<Button>(R.id.btnJam17)
        val btnJam19 = findViewById<Button>(R.id.btnJam19)
        val btnJam21 = findViewById<Button>(R.id.btnJam21)

        btnJam08.setOnClickListener {
            selectTime("08.00")
        }

        btnJam10.setOnClickListener {
            selectTime("10.00")
        }

        btnJam13.setOnClickListener {
            selectTime("13.00")
        }

        btnJam15.setOnClickListener {
            selectTime("15.00")
        }

        btnJam17.setOnClickListener {
            selectTime("17.00")
        }

        btnJam19.setOnClickListener {
            selectTime("19.00")
        }

        btnJam21.setOnClickListener {
            selectTime("21.00")
        }

        val btnKonfirmasi = findViewById<Button>(R.id.btnKonfirmasi)

        btnKonfirmasi.setOnClickListener {

            if (tvSummaryDate.text == "Tanggal belum dipilih") {

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

            Toast.makeText(
                this,
                "Booking berhasil dikonfirmasi!",
                Toast.LENGTH_LONG
            ).show()
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
                    selectedDay
                )

                val dateFormat = SimpleDateFormat(
                    "dd MMMM yyyy",
                    Locale("id", "ID")
                )

                val formattedDate =
                    dateFormat.format(selectedCalendar.time)

                btnTanggal.text = formattedDate

                tvSummaryDate.text = formattedDate

            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
    private fun selectTime(time: String) {

        selectedTime = time

        tvSummaryTime.text = "$time WIB"

        Toast.makeText(
            this,
            "Jam $time dipilih",
            Toast.LENGTH_SHORT
        ).show()
    }
}