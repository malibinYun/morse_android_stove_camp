package com.malibin.morse.presentation.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malibin.morse.data.entity.Room
import com.malibin.morse.data.repository.RoomsRepository
import kotlinx.coroutines.launch

/**
 * Created By Malibin
 * on 2월 05, 2021
 */

class SearchViewModel @ViewModelInject constructor(
    private val roomsRepository: RoomsRepository,
) : ViewModel() {

    val keywordText = MutableLiveData("")

    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> = _rooms

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun searchRooms() = viewModelScope.launch {
        val keyword = keywordText.value.orEmpty()
        val searchedRooms = roomsRepository.searchRooms(keyword)
        _rooms.postValue(searchedRooms)
    }
}
