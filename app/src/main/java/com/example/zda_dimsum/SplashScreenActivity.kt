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

        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {

            // Menampilkan splash screen selama 2 detik
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

            // Mengecek status login
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
                !sudahOnboarding -> OnboardingActivity::class.java
                isLogin -> BaseActivity::class.java
                else -> AuthActivity::class.java
            }

            startActivity(
                Intent(this@SplashScreenActivity, tujuan)
            )

            finish()
        }
    }
}
