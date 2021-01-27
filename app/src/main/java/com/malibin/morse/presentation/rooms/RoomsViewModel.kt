package com.malibin.morse.presentation.rooms

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malibin.morse.data.entity.Room
import com.malibin.morse.data.repository.RoomsRepository
import kotlinx.coroutines.launch

class RoomsViewModel @ViewModelInject constructor(
    private val roomsRepository: RoomsRepository,
) : ViewModel() {
    private val _rooms = MutableLiveData<List<Room>>(emptyList())
    val rooms: LiveData<List<Room>> = _rooms

    fun loadAllRooms() = viewModelScope.launch {
        val allRooms = roomsRepository.getAllRooms()
        _rooms.postValue(allRooms)
    }

    fun removeRoom(room: Room) {
        val currentRooms = getCurrentRooms()
        _rooms.value = currentRooms.apply { remove(room) }
    }

    private fun getCurrentRooms(): MutableList<Room> {
        return _rooms.value?.toMutableList() ?: mutableListOf()
    }
}
