package com.example.occuhelp.ourservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.occuhelp.R
import com.example.occuhelp.databinding.ActivityOurServiceBinding

class OurServiceActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOurServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOurServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}