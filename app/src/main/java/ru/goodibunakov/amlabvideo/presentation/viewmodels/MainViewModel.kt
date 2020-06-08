package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase

class MainViewModel(
        getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase,
        getNetworkStatus: GetNetworkStatusUseCase,
        startToolbarTitle: String
) : BaseActivityViewModel(getNetworkStatus, getChannelPlaylistsUseCase) {

    companion object {
        const val ALL_VIDEOS = "0"
    }

    private var compositeDisposable = CompositeDisposable()
    val toolbarTitleLiveData = MutableLiveData(startToolbarTitle)

    init {

    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}