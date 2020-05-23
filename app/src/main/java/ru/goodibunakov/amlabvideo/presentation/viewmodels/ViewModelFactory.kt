package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetPlaylistVideosUseCase

class ViewModelFactory(
        private val getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase,
        private val getNetworkStatus: GetNetworkStatusUseCase,
        private val getPlaylistVideos: GetPlaylistVideosUseCase
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SplashViewModel::class.java))
            return SplashViewModel(getChannelPlaylistsUseCase, getNetworkStatus) as T

        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(getChannelPlaylistsUseCase, getNetworkStatus) as T

        if (modelClass.isAssignableFrom(SharedViewModel::class.java))
            return SharedViewModel() as T

        if (modelClass.isAssignableFrom(VideoViewModel::class.java))
            return VideoViewModel(getPlaylistVideos) as T

        return super.create(modelClass)
    }
}