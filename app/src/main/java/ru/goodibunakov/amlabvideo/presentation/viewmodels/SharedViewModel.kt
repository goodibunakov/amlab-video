package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    var playlistId: MutableLiveData<String> = MutableLiveData()
        set(value) {
            if (value != field) field = value
        }

    init {
        playlistId.value = ALL_VIDEOS
    }

    companion object {
        const val ALL_VIDEOS = "0"
    }
}