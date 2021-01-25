package com.malibin.morse.presentation.signup

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.malibin.morse.databinding.ActivitySignUpBinding
import com.malibin.morse.presentation.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private var binding: ActivitySignUpBinding? = null
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        this.binding = binding
        setContentView(binding.root)

        initView(binding)
    }

    private fun initView(binding: ActivitySignUpBinding) {
        binding.pagerSignup.adapter = SignUpPagerAdapter(this)
        binding.pagerSignup.isUserInputEnabled = false

        signUpViewModel.pagerPosition.observe(this) {
            binding.pagerSignup.currentItem = it ?: return@observe
        }
        signUpViewModel.toastMessage.observe(this) { showToast(it) }
        signUpViewModel.errorMessage.observe(this) { showToast(it) }
    }

    override fun onBackPressed() {
        if (signUpViewModel.getCurrentPagerPosition() == 0) {
            super.onBackPressed()
            return
        }
        signUpViewModel.goPreviousPage()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    inner class SignUpPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = SignUpViewModel.PAGE_COUNT

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> EmailVerifyFragment()
            1 -> SignUpFragment()
            else -> error("do not exist fragment of position $position")
        }
    }
}
