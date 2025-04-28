package com.example.occuhelp.resetpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.occuhelp.R
import com.example.occuhelp.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}