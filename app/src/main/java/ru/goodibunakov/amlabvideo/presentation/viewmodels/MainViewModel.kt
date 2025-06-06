package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase

class MainViewModel(
    getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase,
    getNetworkStatus: GetNetworkStatusUseCase
) : BaseActivityViewModel(getNetworkStatus, getChannelPlaylistsUseCase) {


    val drawerInitializedLiveData = MutableLiveData(false)

    val toolbarTitleLiveData = MutableLiveData<String>()
}