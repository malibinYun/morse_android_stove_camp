package com.malibin.morse.presentation.viewer

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.malibin.morse.R
import com.malibin.morse.databinding.DialogBroadcastFinishBinding

/**
 * Created By Malibin
 * on 2월 05, 2021
 */

class BroadCastFinishedDialog(context: Context) : Dialog(context) {

    var onButtonClickListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DialogBroadcastFinishBinding.inflate(layoutInflater).apply {
            buttonBack.setOnClickListener {
                onButtonClickListener?.invoke()
                dismiss()
            }
        }
        setContentView(binding.root)
        setCancelable(false)
        window?.setBackgroundDrawableResource(R.color.transparent)
    }
}
