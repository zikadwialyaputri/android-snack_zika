package com.example.zda_dimsum

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.zda_dimsum.onboarding.OnboardingActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menampilkan desain Splash Screen Lapangin
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {

            // Splash Screen tampil selama 2 detik
            delay(2000)

            // Mengecek apakah onboarding sudah selesai
            val onboardingPref = getSharedPreferences(
                "lapangin",
                MODE_PRIVATE
            )

            val sudahOnboarding = onboardingPref.getBoolean(
                "onboarding_selesai",
                false
            )

            // Mengecek apakah pengguna sudah login
            val loginPref = getSharedPreferences(
                "user_pref",
                MODE_PRIVATE
            )

            val isLogin = loginPref.getBoolean(
                "isLogin",
                false
            )

            // Menentukan halaman tujuan
            val tujuan = when {

                // Pengguna belum menyelesaikan onboarding
                !sudahOnboarding -> {
                    OnboardingActivity::class.java
                }

                // Sudah onboarding dan sudah login
                isLogin -> {
                    BaseActivity::class.java
                }

                // Sudah onboarding tetapi belum login
                else -> {
                    AuthActivity::class.java
                }
            }

            // Membuka halaman tujuan
            val intent = Intent(
                this@SplashScreenActivity,
                tujuan
            )

            startActivity(intent)

            // Menutup Splash Screen
            finish()
        }
    }
}
