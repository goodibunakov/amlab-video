package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    var playlistId: MutableLiveData<String> = MutableLiveData()
    val isInPictureInPictureMode: MutableLiveData<Boolean> = MutableLiveData(false)
}