
package com.example.zda_dimsum

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.zda_dimsum.data.api.SupabaseAuth
import com.example.zda_dimsum.databinding.ActivityAuthBinding
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences(
            "user_pref",
            MODE_PRIVATE
        )

        val registeredEmail = intent.getStringExtra(
            "registered_email"
        )

        if (!registeredEmail.isNullOrBlank()) {
            binding.etUsername.setText(registeredEmail)
        }

        binding.btnRegister.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegistrationActivity::class.java
                )
            )
        }

        binding.btnLogin.setOnClickListener {

            val email = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(
                    this,
                    "Email dan password harus diisi",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email).matches()
            ) {
                binding.etUsername.error =
                    "Masukkan email yang valid"
                return@setOnClickListener
            }

            loginKeDatabase(email, password, sharedPref)
        }
    }

    private fun loginKeDatabase(
        email: String,
        password: String,
        sharedPref: android.content.SharedPreferences
    ) {

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Memproses..."

        lifecycleScope.launch {

            try {

                val response = SupabaseAuth.login(
                    email = email,
                    password = password
                )

                val user = response.optJSONObject("user")
                    ?: throw Exception(
                        "Data pengguna tidak ditemukan"
                    )

                val userId = user.optString("id")

                if (userId.isBlank()) {
                    throw Exception("ID pengguna tidak ditemukan")
                }

                sharedPref.edit().apply {
                    putString("user_id", userId)
                    putString("nama", user.optString("nama"))
                    putString("email", user.optString("email"))
                    putString("username", user.optString("username"))
                    putBoolean("isLogin", true)

                    // Hapus token lama dari sistem Supabase Auth
                    remove("access_token")
                    remove("refresh_token")

                    apply()
                }

                Toast.makeText(
                    this@AuthActivity,
                    "Login berhasil!",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this@AuthActivity,
                        BaseActivity::class.java
                    )
                )

                finish()

            } catch (e: Exception) {

                Toast.makeText(
                    this@AuthActivity,
                    "Login gagal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "Masuk"
            }
        }
    }
}
