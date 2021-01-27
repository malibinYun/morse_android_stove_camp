package com.malibin.morse.presentation.chatting

import kotlin.random.Random

/**
 * Created By Malibin
 * on 1월 27, 2021
 */

class RandomColorGenerator : ColorGenerator {
    override fun createColorCode(): String {
        val red = Random.nextInt(0, 256)
        val green = Random.nextInt(0, 256)
        val blue = Random.nextInt(0, 256)
        return "#%X%X%X".format(red, green, blue)
    }
}
