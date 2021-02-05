package com.malibin.morse.data.repository

import com.malibin.morse.data.entity.Room
import com.malibin.morse.data.service.MorseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomsRepository @Inject constructor(
    private val morseService: MorseService,
) {
    suspend fun getAllRooms(): List<Room> = withContext(Dispatchers.IO) {
        val response = morseService.getAllRooms()
        return@withContext response.data
    }

    suspend fun searchRooms(keyword: String): List<Room> = withContext(Dispatchers.IO) {
        val response = morseService.searchRooms(keyword)
        return@withContext response.data
    }
}
