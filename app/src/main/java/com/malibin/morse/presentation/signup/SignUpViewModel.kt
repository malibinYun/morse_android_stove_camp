package com.malibin.morse.presentation.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created By Malibin
 * on 1월 11, 2021
 */

class SignUpViewModel : ViewModel() {

    val pagerPosition = MutableLiveData(0)

    fun goNextPage() {
        if (getCurrentPagerPosition() == MAX_PAGE_INDEX) return
        pagerPosition.value = getCurrentPagerPosition() + 1
    }

    fun goPreviousPage() {
        if (getCurrentPagerPosition() == 0) return
        pagerPosition.value = getCurrentPagerPosition() - 1
    }

    fun getCurrentPagerPosition(): Int = pagerPosition.value
        ?: error("pagerPosition cannot be null")

    companion object {
        const val PAGE_COUNT = 2
        const val MAX_PAGE_INDEX = PAGE_COUNT - 1
    }
}
