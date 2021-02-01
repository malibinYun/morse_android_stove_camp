package com.malibin.morse.data.repository

import com.malibin.morse.data.entity.ChatMessage
import com.malibin.morse.data.service.MorseService
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
    suspend fun sendChatMessage(roomIdx: Int, chatMessage: ChatMessage) {
        withContext(Dispatchers.IO) {
            val params = chatMessage.toSendChatMessageParams(roomIdx)
            morseService.sendChatMessage(params)
        }
    }
}
