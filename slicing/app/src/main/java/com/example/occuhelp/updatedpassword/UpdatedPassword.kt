package com.example.occuhelp.updatedpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.occuhelp.R
import com.example.occuhelp.databinding.ActivityUpdatedPasswordBinding

class UpdatedPassword : AppCompatActivity() {

    private lateinit var binding : ActivityUpdatedPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatedPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}