package com.malibin.morse.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.malibin.morse.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
