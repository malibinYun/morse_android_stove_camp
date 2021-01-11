package com.malibin.morse.presentation.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.malibin.morse.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created By Malibin
 * on 1월 08, 2021
 */

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignupBinding.inflate(inflater, container, false)
        initView(binding)
        return binding.root
    }

    private fun initView(binding: FragmentSignupBinding) {
        binding.lifecycleOwner = activity
        binding.viewModel = signUpViewModel
    }

}
