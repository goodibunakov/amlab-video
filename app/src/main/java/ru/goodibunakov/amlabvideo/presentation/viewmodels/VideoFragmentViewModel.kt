package ru.goodibunakov.amlabvideo.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
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
    val videoDetails = MutableLiveData<VideoDetailsUI>()
    var videoIdSubject = BehaviorSubject.createDefault("")

    init {
        loadVideoDetails()
    }

    fun loadPlaylist(playlistId: String) {
        getPlaylistVideosUseCase.set(playlistId)
        getPlaylistVideosUseCase.buildObservable()
                .map { ToVideoModelUIMapper.map(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    progressBarVisibilityLiveData.value = true
                    Log.d("debug", "getPlaylistVideosUseCase doOnSubscribe")
                }
                .doOnError {
                    progressBarVisibilityLiveData.value = false
                    Log.d("debug", "getPlaylistVideosUseCase doOnError")
                }
                .doOnNext {
                    progressBarVisibilityLiveData.value = false
                    Log.d("debug", "getPlaylistVideosUseCase doOnNext")
                }
                .subscribe({
                    Log.d("ddd", "loadPlaylist $it")
                    videoIdSubject.onNext(it.firstOrNull()?.videoId ?: "")
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
                .doOnSubscribe {
                    progressBarVisibilityLiveData.value = true
                    Log.d("debug", "getAllVideosList doOnSubscribe")
                }
                .doOnError {
                    progressBarVisibilityLiveData.value = false
                    Log.d("debug", "getAllVideosList doOnError $it")
                }
                .doOnNext {
                    progressBarVisibilityLiveData.value = false
                    Log.d("debug", "getAllVideosList doOnNext")
                }
                .subscribe({
                    videoIdSubject.onNext(it.firstOrNull()?.videoId ?: "")
                    videosLiveData.value = it
                }, {

                })
                .addTo(compositeDisposable)
    }

    private fun loadVideoDetails() {
        videoIdSubject.filter { it.isNotEmpty() }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { id ->
                    Log.d("ddd", "id = $id")
                    getVideoDetailsUseCase.set(id)
                    getVideoDetailsUseCase.buildObservable()
                }
                .map { ToVideoDetailsModelUIMapper.map(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("debug", "loadVideoDetails $it")
                    videoDetails.value = it
                }, {
                    Log.d("debug", "loadVideoDetails error = ${it.localizedMessage}")
                })
                .addTo(compositeDisposable)
    }


    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}