package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.goodibunakov.amlabvideo.presentation.activity.MainActivity

class SharedViewModel : ViewModel() {

    var playlistId: MutableLiveData<String> = MutableLiveData()
        set(value) {
            if (value != field && !(value as String).contains(MainActivity.APP_MENU_ITEM))
                field = value
        }

    init {
        playlistId.value = ALL_VIDEOS
    }

    companion object {
        const val ALL_VIDEOS = "0"
    }
}