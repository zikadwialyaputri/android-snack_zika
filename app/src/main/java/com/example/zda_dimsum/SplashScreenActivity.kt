package com.example.zda_dimsum

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {

            // Menampilkan splash screen selama 2 detik
            delay(2000)

            // Mengambil data login dari SharedPreferences
            val sharedPref = getSharedPreferences(
                "user_pref",
                MODE_PRIVATE
            )

            val isLogin = sharedPref.getBoolean(
                "isLogin",
                false
            )

            // Menentukan halaman tujuan berdasarkan status login
            val tujuan = if (isLogin) {
                BaseActivity::class.java
            } else {
                AuthActivity::class.java
            }

            // Berpindah ke halaman tujuan
            val intent = Intent(
                this@SplashScreenActivity,
                tujuan
            )

            startActivity(intent)
            finish()
        }
    }
}