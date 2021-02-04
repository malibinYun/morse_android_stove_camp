package com.malibin.morse.data.repository

import com.malibin.morse.data.entity.ReplayVideo
import com.malibin.morse.data.service.MorseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created By Malibin
 * on 2월 04, 2021
 */

class ReplayVideosRepository @Inject constructor(
    private val morseService: MorseService,
) {
    suspend fun getReplayVideos(): List<ReplayVideo> = withContext(Dispatchers.IO) {
        return@withContext morseService.getReplayVideos()
            .data.map { it.toReplayVideo() }
    }
}
