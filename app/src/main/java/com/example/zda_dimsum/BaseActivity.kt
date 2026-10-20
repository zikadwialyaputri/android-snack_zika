package com.example.zda_dimsum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.zda_dimsum.booking.BookingFragment
import com.example.zda_dimsum.history.HistoryFragment
import com.example.zda_dimsum.home.HomeFragment
import com.example.zda_dimsum.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BaseActivity : AppCompatActivity() {

    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        bottomNavView = findViewById(R.id.bottom_nav_view)

        // Menampilkan Beranda ketika aplikasi pertama dibuka
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        bottomNavView.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.nav_booking -> {
                    replaceFragment(BookingFragment())
                    true
                }

                R.id.nav_history -> {
                    replaceFragment(HistoryFragment())
                    true
                }

                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}