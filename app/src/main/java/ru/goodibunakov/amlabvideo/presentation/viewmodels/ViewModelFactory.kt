package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.goodibunakov.amlabvideo.domain.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.GetNetworkStatusUseCase

class ViewModelFactory(
        private val getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase,
        private val getNetworkStatus: GetNetworkStatusUseCase
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SplashViewModel::class.java))
            return SplashViewModel(getChannelPlaylistsUseCase, getNetworkStatus) as T

        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(getChannelPlaylistsUseCase, getNetworkStatus) as T

        return super.create(modelClass)
    }
}