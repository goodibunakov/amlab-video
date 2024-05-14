package ru.goodibunakov.amlabvideo.presentation.viewmodels

import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase

class SplashViewModel(
    getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase,
    getNetworkStatus: GetNetworkStatusUseCase
) : BaseActivityViewModel(getNetworkStatus, getChannelPlaylistsUseCase) {

//    private var compositeDisposable = CompositeDisposable()
//
//    override fun onCleared() {
//        compositeDisposable.dispose()
//        super.onCleared()
//    }
}