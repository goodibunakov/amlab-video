package ru.goodibunakov.amlabvideo.presentation.viewmodels

import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase

class SplashViewModel(
    getChannelPlaylists: GetChannelPlaylistsUseCase,
    getNetworkStatus: GetNetworkStatusUseCase
) : BaseActivityViewModel(getNetworkStatus, getChannelPlaylists)