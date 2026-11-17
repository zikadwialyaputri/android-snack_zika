package com.example.zda_dimsum.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.zda_dimsum.AuthActivity
import com.example.zda_dimsum.BaseActivity
import com.example.zda_dimsum.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPagerOnboarding.adapter =
            OnboardingAdapter(this)

        binding.viewPagerOnboarding.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    binding.tvIndikator.text = when (position) {
                        0 -> "● ○ ○"
                        1 -> "○ ● ○"
                        else -> "○ ○ ●"
                    }

                    binding.btnLanjut.text =
                        if (position == 2) {
                            "Mulai Sekarang"
                        } else {
                            "Selanjutnya"
                        }
                }
            }
        )

        binding.btnLanjut.setOnClickListener {

            val halaman = binding.viewPagerOnboarding.currentItem

            if (halaman < 2) {
                binding.viewPagerOnboarding.currentItem = halaman + 1
            } else {

                // Menyimpan bahwa onboarding sudah selesai
                getSharedPreferences("lapangin", MODE_PRIVATE)
                    .edit()
                    .putBoolean("onboarding_selesai", true)
                    .apply()

                // Mengecek status login
                val isLogin = getSharedPreferences(
                    "user_pref",
                    MODE_PRIVATE
                ).getBoolean("isLogin", false)

                val tujuan = if (isLogin) {
                    BaseActivity::class.java
                } else {
                    AuthActivity::class.java
                }

                startActivity(
                    Intent(this@OnboardingActivity, tujuan)
                )

                finish()
            }
        }
    }
}
