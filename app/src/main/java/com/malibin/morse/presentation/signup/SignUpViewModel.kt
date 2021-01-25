package com.malibin.morse.presentation.signup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malibin.morse.R
import com.malibin.morse.data.repository.AuthRepository
import com.malibin.morse.data.service.HttpExceptionHandler
import com.malibin.morse.presentation.utils.SingleLiveEvent
import com.malibin.morse.presentation.utils.printLog
import com.malibin.morse.presentation.utils.showToast
import kotlinx.coroutines.launch

/**
 * Created By Malibin
 * on 1월 11, 2021
 */

class SignUpViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val pagerPosition = MutableLiveData(0)
    val email = MutableLiveData("")
    val verifyNumber = MutableLiveData("")
    val nickname = MutableLiveData("")
    val password = MutableLiveData("")
    val passwordCheck = MutableLiveData("")

    val toastMessage = SingleLiveEvent<Int>()
    val errorMessage = SingleLiveEvent<String>()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isVerified = MutableLiveData<Boolean>()
    val isVerified: LiveData<Boolean> = _isVerified

    private fun getEmail() = email.value ?: error("email cannot be null")
    private fun getVerifyNumber() = verifyNumber.value ?: error("verifyNumber cannot be null")
    private fun getNickname() = nickname.value ?: error("nickname cannot be null")
    private fun getPassword() = password.value ?: error("password cannot be null")
    private fun getPasswordCheck() = passwordCheck.value ?: error("passwordCheck cannot be null")

    fun goSignUpNextStep() = when {
        getEmail().isBlank() -> toastMessage.value = R.string.input_email
        getVerifyNumber().isBlank() -> toastMessage.value = R.string.input_verify_number
        else -> goNextPage()
    }

    private fun goNextPage() {
        if (getCurrentPagerPosition() == MAX_PAGE_INDEX) return
        pagerPosition.value = getCurrentPagerPosition() + 1
    }

    fun goPreviousPage() {
        if (getCurrentPagerPosition() == 0) return
        pagerPosition.value = getCurrentPagerPosition() - 1
    }

    fun getCurrentPagerPosition(): Int = pagerPosition.value
        ?: error("pagerPosition cannot be null")

    fun checkEmail() = viewModelScope.launch(handleHttpError()) {
        _isLoading.value = true
        val inputEmail = getEmail()
        if (inputEmail.isBlank()) {
            toastMessage.value = R.string.input_email
            _isLoading.value = false
            return@launch
        }
        authRepository.checkEmail(inputEmail)
        toastMessage.value = R.string.send_email_verify_code
        _isLoading.value = false
    }

    fun verifyEmail() = viewModelScope.launch(handleHttpError()) {
        _isLoading.value = true
        val inputVerifyNumber = getVerifyNumber()
        if (inputVerifyNumber.isBlank()) {
            toastMessage.value = R.string.input_verify_number
            _isLoading.value = false
            return@launch
        }
        authRepository.verifyEmail(getEmail(), inputVerifyNumber)
        toastMessage.value = R.string.verify_complete
        _isVerified.value = true
        _isLoading.value = false
    }

    fun submitSignUp() = when {
        getEmail().isBlank() -> toastMessage.value = R.string.input_email
        getVerifyNumber().isBlank() -> toastMessage.value = R.string.input_verify_number
        getNickname().isBlank() -> toastMessage.value = R.string.input_nickname
        getPassword().isBlank() -> toastMessage.value = R.string.input_password
        isPasswordsNotEqual() -> toastMessage.value = R.string.passwords_not_equal
        else -> {
            // TODO 실제 회원가입 로직
        }
    }

    private fun handleHttpError() = HttpExceptionHandler {
        printLog(it)
        errorMessage.value = it.message
        _isLoading.value = false
    }

    private fun isPasswordsNotEqual(): Boolean {
        return password.value != passwordCheck.value
    }

    //    private fun isNotVerifyEmail(email: String): Boolean {
    //        return !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    //    }
    //
    //    private fun isNotVerifyPassword(password: String): Boolean {
    //        password
    //    }

    companion object {
        const val PAGE_COUNT = 2
        const val MAX_PAGE_INDEX = PAGE_COUNT - 1
    }
}
