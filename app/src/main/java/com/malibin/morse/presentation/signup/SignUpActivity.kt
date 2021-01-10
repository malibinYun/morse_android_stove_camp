package com.malibin.morse.presentation.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.malibin.morse.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pagerSignup.adapter = SignUpPagerAdapter(this)
    }

    companion object {
        private const val PAGE_COUNT = 2
    }

    inner class SignUpPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = PAGE_COUNT

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> EmailVerifyFragment()
            1 -> SignUpFragment()
            else -> error("do not exist fragment of position $position")
        }
    }
}
