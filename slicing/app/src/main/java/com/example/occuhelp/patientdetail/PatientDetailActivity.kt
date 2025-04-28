package com.example.occuhelp.patientdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.occuhelp.R
import com.example.occuhelp.databinding.ActivityPatientDetailBinding

class PatientDetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPatientDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}