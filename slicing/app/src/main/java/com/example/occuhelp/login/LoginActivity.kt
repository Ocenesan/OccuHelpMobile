package com.example.occuhelp.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.occuhelp.R
import com.example.occuhelp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}