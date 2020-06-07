package ru.goodibunakov.amlabvideo.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase
import ru.goodibunakov.amlabvideo.presentation.mappers.ToPlaylistsModelUIMapper
import ru.goodibunakov.amlabvideo.presentation.model.PlaylistsModelUI

class MainViewModel(
        getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase,
        getNetworkStatus: GetNetworkStatusUseCase,
        startToolbarTitle: String
) : ViewModel() {

    companion object {
        const val ALL_VIDEOS = "0"
    }

    private var compositeDisposable = CompositeDisposable()
    val playlistsLiveData = MutableLiveData<List<PlaylistsModelUI>>()
    val error = SingleLiveEvent<Boolean>().apply { this.value = false }
    val networkLiveData = MutableLiveData<ConnectedStatus>()
    val toolbarTitleLiveData = MutableLiveData(startToolbarTitle)


    init {
        Log.d("debug", "toolbarTitleLiveData in MainViewModel = ${toolbarTitleLiveData.value}")
        getChannelPlaylistsUseCase.buildObservable()
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