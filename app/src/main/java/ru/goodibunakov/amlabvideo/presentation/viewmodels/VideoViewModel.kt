package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.domain.usecase.GetPlaylistVideosUseCase
import ru.goodibunakov.amlabvideo.presentation.mappers.ToVideoModelUIMapper
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

class VideoViewModel(private val getPlaylistVideosUseCase: GetPlaylistVideosUseCase) : ViewModel() {

    private lateinit var disposable: Disposable
    val videosLiveData: MutableLiveData<List<VideoUIModel>> = MutableLiveData()

    fun loadPlaylist(playlistId: String) {
        getPlaylistVideosUseCase.set(playlistId)
        disposable = getPlaylistVideosUseCase.buildObservable()
                .subscribeOn(Schedulers.io())
                .map { ToVideoModelUIMapper.map(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    videosLiveData.value = it
                }, {

                })
    }


    override fun onCleared() {
        super.onCleared()
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
    }
}