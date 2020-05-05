package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.domain.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.presentation.mappers.ToPlaylistsModelUIMapper
import ru.goodibunakov.amlabvideo.presentation.model.PlaylistsModelUI

class SplashViewModel(getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase) : ViewModel() {

    private var disposable: Disposable
    val playlistsLiveData = MutableLiveData<List<PlaylistsModelUI>>()
    val error = SingleLiveEvent<Boolean>()

    init {
        disposable = getChannelPlaylistsUseCase.buildObservable()
                .subscribeOn(Schedulers.io())
                .map { ToPlaylistsModelUIMapper().map(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    playlistsLiveData.value = it
                }, {
                    error.value = true
                })
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposable.isDisposed) disposable.dispose()
    }
}