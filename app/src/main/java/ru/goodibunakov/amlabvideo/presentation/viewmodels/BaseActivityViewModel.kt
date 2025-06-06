package ru.goodibunakov.amlabvideo.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.data.QuotaExceedReceiver
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase
import ru.goodibunakov.amlabvideo.presentation.mappers.ToPlaylistsModelUIMapper
import ru.goodibunakov.amlabvideo.presentation.model.PlaylistsModelUI

open class BaseActivityViewModel(
    private val getNetworkStatus: GetNetworkStatusUseCase,
    private val getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase
) : ViewModel() {

    val networkLiveData = MutableLiveData<ConnectedStatus>()
    val playlistsLiveData = MutableLiveData<List<PlaylistsModelUI>>()
    val playlistsUpdatedLiveData = MutableLiveData<Throwable?>()
    val error = SingleLiveEvent<Throwable?>().apply { this.value = null }
    val errorQuotaLiveData = MutableLiveData(false)
    val responseQuotaReceiver = MutableLiveData(QuotaExceedReceiver(errorQuotaLiveData))

    private var compositeDisposable = CompositeDisposable()

    init {
        networkStatusSubscribe()
        getChannelPlaylistsSubscribe()
    }

    private fun getChannelPlaylistsSubscribe() {
        getChannelPlaylistsUseCase.buildObservable()
            .map { ToPlaylistsModelUIMapper.map(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    playlistsLiveData.value = it
                    error.value = null
                }, {
                    error.value = it
                }, { }
            )
            .addTo(compositeDisposable)
    }

    private fun networkStatusSubscribe() {
        getNetworkStatus.buildObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                networkLiveData.value = it
            }, {
            })
            .addTo(compositeDisposable)
    }

    fun updatePlaylistsToDatabase() {
        getChannelPlaylistsUseCase.updatePlaylistsToDatabase()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .map { ToPlaylistsModelUIMapper.map(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                playlistsLiveData.value = it
                playlistsUpdatedLiveData.value = null
            }, {
                playlistsUpdatedLiveData.value = it
            })
            .addTo(compositeDisposable)

    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}