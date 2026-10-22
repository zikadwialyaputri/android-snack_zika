
package com.example.zda_dimsum

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class RegistrationActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvTanggalLahir: TextView
    private lateinit var rgJenisKelamin: RadioGroup
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

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

        val btnPilihTanggal =
            findViewById<Button>(R.id.btnPilihTanggal)
        val btnSelanjutnya =
            findViewById<Button>(R.id.btnSelanjutnya)
        btnPilihTanggal.setOnClickListener {
            tampilkanDatePicker()
        }
        btnSelanjutnya.setOnClickListener {
            simpanDataRegistrasi()
        }
    }

    private fun tampilkanDatePicker() {
        val kalender = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, tahun, bulan, hari ->

                tanggalLahir = String.format(
                    "%02d/%02d/%04d",
                    hari,
                    bulan + 1,
                    tahun
                )

                tvTanggalLahir.text = tanggalLahir
            },
            kalender.get(Calendar.YEAR),
            kalender.get(Calendar.MONTH),
            kalender.get(Calendar.DAY_OF_MONTH)
        )

        // Tanggal lahir tidak boleh melewati hari ini
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun simpanDataRegistrasi() {

        val nama = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        val idJenisKelamin = rgJenisKelamin.checkedRadioButtonId

        val jenisKelamin = if (idJenisKelamin != -1) {
            findViewById<RadioButton>(
                idJenisKelamin
            ).text.toString()
        } else {
            ""
        }

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
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Format email tidak valid"
            etEmail.requestFocus()
            return
        }
        if (password != confirmPassword) {
            etConfirmPassword.error = "Password tidak cocok"
            etConfirmPassword.requestFocus()
            return
        }
        val sharedPref = getSharedPreferences(
            "user_pref",
            MODE_PRIVATE
        )
        sharedPref.edit().apply {
            putString("nama", nama)
            putString("email", email)
            putString("tanggal_lahir", tanggalLahir)
            putString("jenis_kelamin", jenisKelamin)
            putString("username", username)
            putString("password", password)

            // Status login dibuat false setelah registrasi
            putBoolean("isLogin", false)

            apply()
        }

        Toast.makeText(
            this,
            "Data registrasi berhasil disimpan",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(
            Intent(this, VerificationActivity::class.java)
        )
        finish()
    }
}
