
package com.example.zda_dimsum

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VerificationActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etTanggalLahir: EditText
    private lateinit var etJenisKelamin: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etTanggalLahir = findViewById(R.id.etTanggalLahir)
        etJenisKelamin = findViewById(R.id.etJenisKelamin)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        val btnSubmit =
            findViewById<android.widget.Button>(R.id.btnSubmit)

        val btnKembali =
            findViewById<android.widget.Button>(R.id.btnKembali)

        val sharedPref = getSharedPreferences(
            "user_pref",
            MODE_PRIVATE
        )

        // Menampilkan data dari SharedPreferences
        etNama.setText(sharedPref.getString("nama", ""))
        etEmail.setText(sharedPref.getString("email", ""))
        etTanggalLahir.setText(
            sharedPref.getString("tanggal_lahir", "")
        )
        etJenisKelamin.setText(
            sharedPref.getString("jenis_kelamin", "")
        )
        etUsername.setText(sharedPref.getString("username", ""))
        etPassword.setText(sharedPref.getString("password", ""))
        etConfirmPassword.setText(
            sharedPref.getString("confirm_password", "")
        )

        btnKembali.setOnClickListener {
            startActivity(
                Intent(this, RegistrationActivity::class.java)
            )
            finish()
        }

        btnSubmit.setOnClickListener {

            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val tanggalLahir =
                etTanggalLahir.text.toString().trim()
            val jenisKelamin =
                etJenisKelamin.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword =
                etConfirmPassword.text.toString()

            if (
                nama.isEmpty() ||
                email.isEmpty() ||
                tanggalLahir.isEmpty() ||
                jenisKelamin.isEmpty() ||
                username.isEmpty() ||
                password.isEmpty() ||
                confirmPassword.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Semua data harus diisi",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()
            ) {
                etEmail.error = "Format email tidak valid"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                etConfirmPassword.error =
                    "Password dan Confirm Password tidak sama"
                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }
            sharedPref.edit().apply {
                putString("nama", nama)
                putString("email", email)
                putString("tanggal_lahir", tanggalLahir)
                putString("jenis_kelamin", jenisKelamin)
                putString("username", username)
                putString("password", password)
                putString("confirm_password", confirmPassword)
                putBoolean("registrasiBerhasil", true)
                apply()
            }
            startActivity(
                Intent(
                    this,
                    RegistrationSuccessActivity::class.java
                )
            )
            finish()
        }
    }
}
