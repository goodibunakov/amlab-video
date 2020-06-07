package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.goodibunakov.amlabvideo.domain.usecase.*

class ViewModelFactory(
        private val getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase,
        private val getNetworkStatusUseCase: GetNetworkStatusUseCase,
        private val getPlaylistVideosUseCase: GetPlaylistVideosUseCase,
        private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
        private val getAllVideosListUseCase: GetAllVideosListUseCase,
        private val startToolbarTitle: String
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SplashViewModel::class.java))
            return SplashViewModel(getChannelPlaylistsUseCase, getNetworkStatusUseCase) as T

        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(getChannelPlaylistsUseCase, getNetworkStatusUseCase, startToolbarTitle) as T

        if (modelClass.isAssignableFrom(SharedViewModel::class.java))
            return SharedViewModel() as T

        if (modelClass.isAssignableFrom(VideoFragmentViewModel::class.java))
            return VideoFragmentViewModel(getPlaylistVideosUseCase, getVideoDetailsUseCase, getAllVideosListUseCase) as T

        return super.create(modelClass)
    }
}