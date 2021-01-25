package com.malibin.morse.presentation.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malibin.morse.R
import com.malibin.morse.data.repository.AuthRepository
import com.malibin.morse.presentation.utils.HttpExceptionHandler
import com.malibin.morse.presentation.utils.SingleLiveEvent
import com.malibin.morse.presentation.utils.printLog
import kotlinx.coroutines.launch

/**
 * Created By Malibin
 * on 1월 08, 2021
 */

class LoginViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val email = MutableLiveData("")
    val password = MutableLiveData("")

    val isLoading = SingleLiveEvent<Boolean>()
    val isSuccess = SingleLiveEvent<Any>()
    val toastMessage = SingleLiveEvent<Any>()

    fun login() {
        isLoading.value = true
        val email = this.email.value ?: error("email cannot be null")
        val password = this.password.value ?: error("password cannot be null")

        when {
            email.isBlank() -> toastMessage.value = R.string.input_email
            password.isBlank() -> toastMessage.value = R.string.input_password
            else -> viewModelScope.launch(handleLoginFail()) {
                authRepository.login(email, password)
                isLoading.value = false
                isSuccess.call()
            }
        }
    }

    private fun handleLoginFail() = HttpExceptionHandler {
        isLoading.value = false
        toastMessage.value = it.message
        printLog(it)
    }
}
