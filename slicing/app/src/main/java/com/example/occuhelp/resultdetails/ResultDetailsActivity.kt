package com.example.occuhelp.resultdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.occuhelp.R
import com.example.occuhelp.databinding.ActivityResultDetailsBinding

class ResultDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityResultDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}