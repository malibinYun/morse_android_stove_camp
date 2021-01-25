package com.malibin.morse.presentation.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter

/**
 * Created By Malibin
 * on 1월 11, 2021
 */

fun Context.showToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes stringResId: Int?) {
    if (stringResId == null) showToast(null as? String?)
    else Toast.makeText(this, stringResId, Toast.LENGTH_SHORT).show()
}

@BindingAdapter("isEnabled")
fun bindingIsEnabled(view: View, isEnabled: Boolean?) {
    view.isEnabled = isEnabled ?: false
}
