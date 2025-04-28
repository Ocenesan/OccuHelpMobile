package com.example.occuhelp.report

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.occuhelp.R
import com.example.occuhelp.databinding.ActivityReportBinding

class ReportActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}