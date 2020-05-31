package ru.goodibunakov.amlabvideo.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.domain.usecase.GetAllVideosListUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetPlaylistVideosUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetVideoDetailsUseCase
import ru.goodibunakov.amlabvideo.presentation.mappers.ToVideoDetailsModelUIMapper
import ru.goodibunakov.amlabvideo.presentation.mappers.ToVideoModelUIMapper
import ru.goodibunakov.amlabvideo.presentation.model.VideoDetailsUI
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

class VideoFragmentViewModel(
        private val getPlaylistVideosUseCase: GetPlaylistVideosUseCase,
        private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
        private val getAllVideosList: GetAllVideosListUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    val videosLiveData = MutableLiveData<List<VideoUIModel>>()
    val progressBarVisibilityLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val currentVideoLiveData = MutableLiveData<VideoUIModel>()
    val videoDetails = MutableLiveData<VideoDetailsUI>()

    fun loadPlaylist(playlistId: String) {
        getPlaylistVideosUseCase.set(playlistId)
        getPlaylistVideosUseCase.buildObservable()
                .map { ToVideoModelUIMapper.map(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarVisibilityLiveData.value = true }
                .doOnError { progressBarVisibilityLiveData.value = false }
                .doOnNext { progressBarVisibilityLiveData.value = false }
                .subscribe({
                    Log.d("ddd", "loadPlaylist $it")
                    loadVideoDetails(it.firstOrNull()?.videoId ?: "")
                    currentVideoLiveData.value = it.firstOrNull()
                    videosLiveData.value = it
                }, {
                    Log.d("ddd", "loadPlaylist error = ${it.localizedMessage}")
                    Log.d("ddd", "loadPlaylist error = $it")
                    Log.d("ddd", "loadPlaylist error = ${it.message}")
                })
                .addTo(compositeDisposable)
    }

    fun loadAllVideosList() {
        getAllVideosList.buildObservable()
                .map { ToVideoModelUIMapper.map(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarVisibilityLiveData.value = true }
                .doOnError { progressBarVisibilityLiveData.value = false }
                .doOnNext { progressBarVisibilityLiveData.value = false }
                .subscribe({
                    Log.d("debug", "all videos = $it")
                }, {

                })
                .addTo(compositeDisposable)
    }

    private fun loadVideoDetails(id: String) {
        getVideoDetailsUseCase.set(id)
        getVideoDetailsUseCase.buildObservable()
                .map { ToVideoDetailsModelUIMapper.map(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .firstOrError()
                .subscribe({
                    Log.d("ddd", "loadVideoDetails $it")
                    videoDetails.value = it
                }, {
                    Log.d("ddd", "loadVideoDetails error = ${it.localizedMessage}")
                })
                .addTo(compositeDisposable)
    }


    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}