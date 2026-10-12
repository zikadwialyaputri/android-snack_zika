package com.example.zda_dimsum

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.zda_dimsum.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences(
            "user_pref",
            MODE_PRIVATE
        )
        binding.btnMulai.setOnClickListener {

            val intent = Intent(
                this,
                DimsumActivity::class.java
            )

            startActivity(intent)
        }

        /*
        binding.btnDeveloper.setOnClickListener {

            val intent = Intent(
                this,
                DeveloperActivity::class.java
            )

            startActivity(intent)
        }
        */

        binding.btnLogout.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah kamu yakin ingin logout?")

                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }

                .setPositiveButton("Ya") { dialog, _ ->
                    sharedPref.edit()
                        .clear()
                        .apply()

                    dialog.dismiss()

                    val intent = Intent(
                        this,
                        AuthActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()
                }

                .show()
        }
    }
}