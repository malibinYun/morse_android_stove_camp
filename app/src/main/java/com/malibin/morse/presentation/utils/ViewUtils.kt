package com.malibin.morse.presentation.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * Created By Malibin
 * on 1월 11, 2021
 */

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes stringResId: Int) {
    Toast.makeText(this, stringResId, Toast.LENGTH_SHORT).show()
}
