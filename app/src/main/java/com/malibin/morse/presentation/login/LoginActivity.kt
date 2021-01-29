package com.malibin.morse.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.malibin.morse.databinding.ActivityLoginBinding
import com.malibin.morse.presentation.rooms.RoomsActivity
import com.malibin.morse.presentation.signup.SignUpActivity
import com.malibin.morse.presentation.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView(binding)

        loginViewModel.isSuccess.observe(this) { deployRoomsActivity() }
        loginViewModel.toastMessage.observe(this) {
            when (it) {
                is Int -> showToast(it)
                is String -> showToast(it)
            }
        }
        loginViewModel.autoLogin()
    }

    private fun initView(binding: ActivityLoginBinding) {
        binding.viewModel = loginViewModel
        binding.lifecycleOwner = this
        binding.buttonSignup.setOnClickListener { deploySignUpActivity() }
    }

    private fun deploySignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun deployRoomsActivity() {
        val intent = Intent(this, RoomsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
