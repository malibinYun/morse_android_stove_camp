package com.malibin.morse.data.repository

import com.malibin.morse.data.entity.ChatMessage
import com.malibin.morse.data.service.MorseService
import com.malibin.morse.data.service.params.SendChatMessageParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created By Malibin
 * on 1월 29, 2021
 */

class ChatMessageRepository @Inject constructor(
    private val morseService: MorseService,
) {
    suspend fun sendChatMessage(params: SendChatMessageParams) {
        withContext(Dispatchers.IO) {
            morseService.sendChatMessage(params)
        }
    }
}
