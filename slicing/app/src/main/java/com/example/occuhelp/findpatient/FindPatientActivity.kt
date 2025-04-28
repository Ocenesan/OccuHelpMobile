package com.example.occuhelp.findpatient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.occuhelp.R
import com.example.occuhelp.databinding.ActivityFindPatientBinding

class FindPatientActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFindPatientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}