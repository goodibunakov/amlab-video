package ru.goodibunakov.amlabvideo.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase
import ru.goodibunakov.amlabvideo.presentation.mappers.ToPlaylistsModelUIMapper
import ru.goodibunakov.amlabvideo.presentation.model.PlaylistsModelUI

class SplashViewModel(
        getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase,
        getNetworkStatus: GetNetworkStatusUseCase
) : ViewModel() {

    private var compositeDisposable = CompositeDisposable()
    val playlistsLiveData = MutableLiveData<List<PlaylistsModelUI>>()
    val networkLiveData = MutableLiveData<ConnectedStatus>()
    val error = SingleLiveEvent<Boolean>().apply { this.value = false }


    init {
        getChannelPlaylistsUseCase.buildObservable()
                .map { ToPlaylistsModelUIMapper.map(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("ddd", "getChannelPlaylistsUseCase = $it")
                    playlistsLiveData.value = it
                    error.value = false
                }, {
                    error.value = true
                    Log.d("ddd", "getChannelPlaylistsUseCase error = ${it.localizedMessage}, ${it.cause}")
                }, {
                    Log.d("ddd", "getChannelPlaylistsUseCase complete")
                })
                .addTo(compositeDisposable)


        getNetworkStatus.buildObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    networkLiveData.value = it
                }, {

                })
                .addTo(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}