
package com.example.zda_dimsum

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegistrationSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_success)

        findViewById<Button>(R.id.btnKeLogin)
            .setOnClickListener {

                startActivity(
                    Intent(this, AuthActivity::class.java)
                )
                finish()
            }
    }
}
