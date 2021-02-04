package com.malibin.morse.presentation.replay

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malibin.morse.data.entity.ReplayVideo
import com.malibin.morse.data.repository.ReplayVideosRepository
import kotlinx.coroutines.launch

/**
 * Created By Malibin
 * on 2월 04, 2021
 */

class ReplaysViewModel @ViewModelInject constructor(
    private val replayVideosRepository: ReplayVideosRepository,
) : ViewModel() {

    private val _replayVideos = MutableLiveData<List<ReplayVideo>>(emptyList())
    val replayVideos: LiveData<List<ReplayVideo>> = _replayVideos

    fun loadAllReplayVideos() = viewModelScope.launch {
        val allReplayVideos = replayVideosRepository.getReplayVideos()
        _replayVideos.postValue(allReplayVideos)
    }
}
