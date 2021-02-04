package com.malibin.morse.presentation.replay.player

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created By Malibin
 * on 2월 04, 2021
 */

class ReplayViewModel @ViewModelInject constructor(

) : ViewModel() {

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    fun changeLoadingTo(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }
}