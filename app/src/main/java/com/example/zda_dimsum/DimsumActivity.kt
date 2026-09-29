package com.example.zda_dimsum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zda_dimsum.databinding.ActivityDimsumBinding

class DimsumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDimsumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDimsumBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}