package com.example.zda_dimsum

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class DimsumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dimsum)
        val toolbar =
            findViewById<Toolbar>(R.id.toolbarHome)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
        val cardLapangan =
            findViewById<LinearLayout>(R.id.cardLapangan)

        cardLapangan.setOnClickListener {

            val intent = Intent(
                this,
                BookingActivity::class.java
            )

            startActivity(intent)
        }
        val navBooking =
            findViewById<LinearLayout>(R.id.navBooking)

        navBooking.setOnClickListener {

            val intent = Intent(
                this,
                BookingActivity::class.java
            )

            startActivity(intent)
        }
        val btnSmashBooking =
            findViewById<Button>(R.id.btnSmashBooking)

        btnSmashBooking.setOnClickListener {

            val intent = Intent(
                this,
                WebActivity::class.java
            )

            startActivity(intent)
        }
    }
}