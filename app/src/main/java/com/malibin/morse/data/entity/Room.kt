package com.malibin.morse.data.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Room(
    @SerializedName("presenterIdx")
    val id: Int,
    val title: String,
    @SerializedName("contents")
    val description: String,
    val viewerCount: Int,
    @SerializedName("presenterNickname")
    val broadCasterNickname: String,
) : Serializable
