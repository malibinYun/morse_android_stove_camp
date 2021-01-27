package com.malibin.morse.data.repository

import com.malibin.morse.data.entity.Room
import com.malibin.morse.data.service.MorseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomsRepository @Inject constructor(
    private val morseService: MorseService,
) {
//    suspend fun getAllRooms(): List<Room> {
//        withContext(Dispatchers.IO) {
//            val response = morseService.getAllRooms()
//            return@withContext response.data
//        }
//        return emptyList()
//    }

    suspend fun getAllRooms(): List<Room> {
        return listOf(
            Room(0, "말리빈의 방송", "호놀룰루 유튭에이어서 이젠 스트리밍까지?", 1),
            Room(0, "고영희", "역시 방송은 고영희지", 100),
            Room(0, "고스트하스왕", "하스슷톤방송입니다", 500),
        )
    }
}
