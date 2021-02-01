package com.malibin.morse.presentation.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malibin.morse.data.entity.Account
import com.malibin.morse.data.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created By Malibin
 * on 2월 01, 2021
 */

class MyPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _account = MutableLiveData<Account>()
    val account: LiveData<Account> = _account

    fun loadAccount() = viewModelScope.launch {
//        _account.value = authRepository.getAccount()
    }

    fun logout() = viewModelScope.launch {
//        authRepository.deleteTokens()
    }
}