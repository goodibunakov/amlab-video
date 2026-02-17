package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase

class MainViewModel(
    getChannelPlaylists: GetChannelPlaylistsUseCase,
    getNetworkStatus: GetNetworkStatusUseCase
) : BaseActivityViewModel(getNetworkStatus, getChannelPlaylists) {


    val drawerInitializedLiveData = MutableLiveData(false)

    val toolbarTitleLiveData = MutableLiveData<String>()
}