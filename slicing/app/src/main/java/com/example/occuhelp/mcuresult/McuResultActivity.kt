package com.example.occuhelp.mcuresult

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.occuhelp.databinding.ActivityMcuResultBinding

class McuResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMcuResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMcuResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}