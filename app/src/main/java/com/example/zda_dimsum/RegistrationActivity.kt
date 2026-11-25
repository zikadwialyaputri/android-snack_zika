
package com.example.zda_dimsum

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.zda_dimsum.data.api.SupabaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar

class RegistrationActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvTanggalLahir: TextView
    private lateinit var rgJenisKelamin: RadioGroup
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnPilihTanggal: Button
    private lateinit var btnSelanjutnya: Button

    private var tanggalLahir = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        tvTanggalLahir = findViewById(R.id.tvTanggalLahir)
        rgJenisKelamin = findViewById(R.id.rgJenisKelamin)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnPilihTanggal = findViewById(R.id.btnPilihTanggal)
        btnSelanjutnya = findViewById(R.id.btnSelanjutnya)

        btnPilihTanggal.setOnClickListener {
            tampilkanDatePicker()
        }

        btnSelanjutnya.setOnClickListener {
            prosesRegistrasi()
        }
    }

    private fun tampilkanDatePicker() {

        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                tanggalLahir = String.format(
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    dayOfMonth
                )

                tvTanggalLahir.text = tanggalLahir
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.datePicker.maxDate =
            System.currentTimeMillis()

        datePicker.show()
    }

    private fun prosesRegistrasi() {

        val nama = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword =
            etConfirmPassword.text.toString()

        val jenisKelamin =
            when (rgJenisKelamin.checkedRadioButtonId) {
                R.id.rbLakiLaki -> "Laki-laki"
                R.id.rbPerempuan -> "Perempuan"
                else -> ""
            }

        if (
            nama.isBlank() ||
            email.isBlank() ||
            username.isBlank() ||
            tanggalLahir.isBlank() ||
            jenisKelamin.isBlank() ||
            password.isBlank() ||
            confirmPassword.isBlank()
        ) {
            Toast.makeText(
                this,
                "Semua data wajib diisi!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()
        ) {
            etEmail.error = "Format email tidak valid"
            return
        }

        if (password.length < 6) {
            etPassword.error =
                "Password minimal 6 karakter"
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error =
                "Password tidak sama"
            return
        }

        btnSelanjutnya.isEnabled = false
        btnSelanjutnya.text = "Memproses..."

        lifecycleScope.launch {

            try {

                val response = SupabaseAuth.register(
                    nama = nama,
                    email = email,
                    username = username,
                    tanggalLahir = tanggalLahir,
                    jenisKelamin = jenisKelamin,
                    password = password
                )

                val userId = response.optString("user_id")

                if (userId.isBlank()) {
                    throw Exception(
                        "Registrasi belum berhasil. ID pengguna kosong."
                    )
                }

                Toast.makeText(
                    this@RegistrationActivity,
                    "Registrasi berhasil! Silakan login.",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(
                    this@RegistrationActivity,
                    AuthActivity::class.java
                )

                intent.putExtra("registered_email", email)

                startActivity(intent)
                finish()

            } catch (e: Exception) {

                android.util.Log.e(
                    "REGISTRATION_ERROR",
                    "Registrasi gagal",
                    e
                )

                Toast.makeText(
                    this@RegistrationActivity,
                    "Registrasi gagal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                btnSelanjutnya.isEnabled = true
                btnSelanjutnya.text = "Selanjutnya"
            }
        }
    }
}
