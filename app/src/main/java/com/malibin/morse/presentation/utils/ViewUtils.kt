package com.malibin.morse.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import kotlin.random.Random

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

fun Context.getOrientation(): Int {
    return resources.configuration.orientation
}

fun Context.isPortraitOrientation(): Boolean {
    return getOrientation() == Configuration.ORIENTATION_PORTRAIT
}

fun Activity.hideStatusBar() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
}

fun createRandomColorCode(seed: Int = System.currentTimeMillis().toInt()): String {
    val random = Random(seed)
    val red = random.nextInt(0, 256)
    val green = random.nextInt(0, 256)
    val blue = random.nextInt(0, 256)
    return "#%02X%02X%02X".format(red, green, blue)
}

@BindingAdapter("isEnabled")
fun bindingIsEnabled(view: View, isEnabled: Boolean?) {
    view.isEnabled = isEnabled ?: false
}

@BindingAdapter("isActivated")
fun bindingIsActivated(view: View, isActivated: Boolean?) {
    view.isActivated = isActivated ?: false
}
