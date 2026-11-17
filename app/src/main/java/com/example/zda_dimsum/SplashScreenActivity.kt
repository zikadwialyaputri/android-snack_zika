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
            delay(2000)

            // Mode demo: selalu membuka onboarding
            val intent = Intent(
                this@SplashScreenActivity,
                OnboardingActivity::class.java
            )

            startActivity(intent)
            finish()
        }
    }
}
