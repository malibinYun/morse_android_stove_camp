package com.malibin.morse.presentation.signup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malibin.morse.R
import com.malibin.morse.data.repository.AuthRepository
import com.malibin.morse.presentation.utils.SingleLiveEvent
import com.malibin.morse.presentation.utils.printLog
import kotlinx.coroutines.CoroutineExceptionHandler
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

    fun checkEmail() = viewModelScope.launch(handleCheckEmailFail()) {
        val inputEmail = email.value ?: error("email cannot be null")
        if (inputEmail.isBlank()) {
            toastMessage.value = R.string.input_email
            return@launch
        }
        authRepository.checkEmail(inputEmail)
    }

    private fun handleCheckEmailFail() = CoroutineExceptionHandler { _, t ->
        printLog("")
        t.printStackTrace()
    }

    fun goSignUpNextStep() = when {
        email.value.isNullOrBlank() -> toastMessage.value = R.string.input_email
        verifyNumber.value.isNullOrBlank() -> toastMessage.value = R.string.input_verify_number
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

    fun submitSignUp() = when {
        email.value.isNullOrBlank() -> toastMessage.value = R.string.input_email
        verifyNumber.value.isNullOrBlank() -> toastMessage.value = R.string.input_verify_number
        nickname.value.isNullOrBlank() -> toastMessage.value = R.string.input_nickname
        password.value.isNullOrBlank() -> toastMessage.value = R.string.input_password
        isPasswordsNotEqual() -> toastMessage.value = R.string.passwords_not_equal
        else -> {
            // TODO 실제 회원가입 로직
        }
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
