package com.example.occuhelp.findmcuresult

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.occuhelp.R
import com.example.occuhelp.databinding.ActivityFindMcuResultBinding

class FindMcuResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFindMcuResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindMcuResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}