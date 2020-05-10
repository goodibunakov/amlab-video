package ru.goodibunakov.amlabvideo.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.domain.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.GetNetworkStatusUseCase
import ru.goodibunakov.amlabvideo.presentation.mappers.ToPlaylistsModelUIMapper
import ru.goodibunakov.amlabvideo.presentation.model.PlaylistsModelUI

class MainViewModel(
        getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase,
        getNetworkStatus: GetNetworkStatusUseCase
): ViewModel() {

    private var disposable: Disposable
    val playlistsLiveData = MutableLiveData<List<PlaylistsModelUI>>()
    val error = SingleLiveEvent<Boolean>().apply { this.value = false }

    init {
        disposable = getChannelPlaylistsUseCase.buildObservable()
                .subscribeOn(Schedulers.io())
                .map { ToPlaylistsModelUIMapper.map(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    playlistsLiveData.value = it
                    error.value = false
                }, {
                    error.value = true
                    Log.d("debug", "error = ${it.localizedMessage}")
                })
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposable.isDisposed) disposable.dispose()
    }
}