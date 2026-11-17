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

        // Mengaktifkan ViewBinding
        binding = ActivityOnboardingBinding.inflate(
            layoutInflater
        )

        setContentView(binding.root)

        // Menghubungkan ViewPager2 dengan adapter
        binding.viewPagerOnboarding.adapter =
            OnboardingAdapter(this)

        // Menampilkan indikator halaman
        binding.viewPagerOnboarding.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    // Mengubah indikator titik
                    binding.tvIndikator.text = when (position) {
                        0 -> "● ○ ○"
                        1 -> "○ ● ○"
                        else -> "○ ○ ●"
                    }

                    // Mengubah tulisan tombol
                    binding.btnLanjut.text =
                        if (position == 2) {
                            "Mulai Sekarang"
                        } else {
                            "Selanjutnya"
                        }
                }
            }
        )

        // Menangani tombol Selanjutnya
        binding.btnLanjut.setOnClickListener {

            val halaman =
                binding.viewPagerOnboarding.currentItem

            if (halaman < 2) {

                // Pindah ke halaman onboarding berikutnya
                binding.viewPagerOnboarding.currentItem =
                    halaman + 1

            } else {

                // Menyimpan status onboarding selesai
                val onboardingPref = getSharedPreferences(
                    "lapangin",
                    MODE_PRIVATE
                )

                onboardingPref.edit()
                    .putBoolean("onboarding_selesai", true)
                    .apply()

                // Memeriksa status login pengguna
                val loginPref = getSharedPreferences(
                    "user_pref",
                    MODE_PRIVATE
                )

                val isLogin = loginPref.getBoolean(
                    "isLogin",
                    false
                )

                // Menentukan halaman tujuan
                val tujuan = if (isLogin) {
                    BaseActivity::class.java
                } else {
                    AuthActivity::class.java
                }

                // Membuka halaman tujuan
                val intent = Intent(
                    this@OnboardingActivity,
                    tujuan
                )

                startActivity(intent)

                // Menutup onboarding
                finish()
            }
        }
    }
}
