package com.example.zda_dimsum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zda_dimsum.databinding.ActivityDeveloperBinding

class DeveloperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeveloperBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeveloperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnKembali.setOnClickListener {
            finish()
        }
    }
}