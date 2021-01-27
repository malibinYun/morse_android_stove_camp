package com.malibin.morse.data.entity

import java.io.Serializable

data class Room(
    val id: Int,
    val title: String,
    val description: String,
    val viewerCount: Int,
) : Serializable
