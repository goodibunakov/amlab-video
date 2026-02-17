package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.goodibunakov.amlabvideo.domain.usecase.DeleteStarFromDbUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetAllVideosListUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetPlaylistVideosUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetStarsFromDbUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetVideoDetailsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.SaveStarToDbUseCase
import ru.goodibunakov.amlabvideo.presentation.activity.MainActivity
import ru.goodibunakov.amlabvideo.presentation.fragments.VideoFragment
import ru.goodibunakov.amlabvideo.presentation.mappers.ToVideoDetailsModelUIMapper
import ru.goodibunakov.amlabvideo.presentation.mappers.ToVideoModelUIMapper
import ru.goodibunakov.amlabvideo.presentation.model.VideoDetailsUI
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

class VideoFragmentViewModel(
    private val getPlaylistVideos: GetPlaylistVideosUseCase,
    private val getVideoDetails: GetVideoDetailsUseCase,
    private val getAllVideosList: GetAllVideosListUseCase,
    private val saveStarToDb: SaveStarToDbUseCase,
    private val deleteStarFromDb: DeleteStarFromDbUseCase,
    private val getStarsFromDb: GetStarsFromDbUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    val videosLiveData = MutableLiveData<List<VideoUIModel>>()
    val progressBarVisibilityLiveData = MutableLiveData(false)
    val videoDetails = MutableLiveData<VideoDetailsUI>()
    val recyclerLoadMoreProcess = MutableLiveData<Boolean>()
    val canLoadMoreLiveData = MutableLiveData(false)
    val error = SingleLiveEvent<Throwable?>().apply { this.value = null }
    var videoIdSubject = BehaviorSubject.createDefault("")
    val videoItemStarChangedLiveData = MutableLiveData<String>()
    private val showInAppReviewSubject = PublishSubject.create<Any>()
    val showInAppReviewLiveData = MutableLiveData(false)

    init {
        loadVideoDetails()

        showInAppReviewSubject
            .doOnNext {
                showInAppReviewLiveData.value = true
            }
            .take(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {})
            .addTo(compositeDisposable)
    }

    fun loadItems(playlistId: String, fragmentType: VideoFragment.FragmentType) {
        when (fragmentType) {
            VideoFragment.FragmentType.FROM_DB -> loadPlaylistItemsFromDb()
            VideoFragment.FragmentType.FROM_WEB -> loadPlaylistItems(playlistId)
        }
    }

    private fun loadPlaylistItemsFromDb() {
        getStarsFromDb.buildObservable()
            .map { ToVideoModelUIMapper.map(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progressBarVisibilityLiveData.value = true }
            .doOnError { progressBarVisibilityLiveData.value = false }
            .doOnNext { progressBarVisibilityLiveData.value = false }
            .subscribe({
                videoIdSubject.onNext(it.firstOrNull()?.videoId ?: "")
                videosLiveData.value = it
                error.value = null
            }, { error.value = it })
            .addTo(compositeDisposable)
    }

    private fun loadPlaylistItems(playlistId: String) {
        if (playlistId.contains(MainActivity.APP_MENU_ITEM)) return
        getPlaylistVideos.set(playlistId)
        getPlaylistVideos.buildObservable()
            .map { ToVideoModelUIMapper.map(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                progressBarVisibilityLiveData.value = true
            }
            .doOnError {
                progressBarVisibilityLiveData.value = false
            }
            .doOnNext {
                progressBarVisibilityLiveData.value = false
                canLoadMoreLiveData.value = getPlaylistVideos.canLoadMore()
            }
            .subscribe({
                videoIdSubject.onNext(it.firstOrNull()?.videoId ?: "")
                videosLiveData.value = it
                error.value = null
            }, {
                error.value = it
            })
            .addTo(compositeDisposable)
    }

    private fun loadMorePlaylist() {
        if (!getPlaylistVideos.canLoadMore()) return
        getPlaylistVideos.buildObservable()
            .map { ToVideoModelUIMapper.map(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { recyclerLoadMoreProcess.value = true }
            .doOnError { recyclerLoadMoreProcess.value = false }
            .subscribe({
                canLoadMoreLiveData.value = getPlaylistVideos.canLoadMore()
                recyclerLoadMoreProcess.value = false
                videosLiveData.value = it
                error.value = null
            }, {
                error.value = it
            })
            .addTo(compositeDisposable)
    }

    private fun loadAllVideosList() {
        getAllVideosList.buildObservable()
            .map { ToVideoModelUIMapper.map(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                progressBarVisibilityLiveData.value = true
            }
            .doOnError {
                progressBarVisibilityLiveData.value = false
            }
            .doOnNext {
                progressBarVisibilityLiveData.value = false
                canLoadMoreLiveData.value = getAllVideosList.canLoadMore()
            }
            .subscribe({
                videoIdSubject.onNext(it.firstOrNull()?.videoId ?: "")
                videosLiveData.value = it
                error.value = null
            }, {
                error.value = it
            })
            .addTo(compositeDisposable)
    }

    private fun loadMoreAllVideosList() {
        if (!getAllVideosList.canLoadMore()) return
        getAllVideosList.getMoreAllVideosList()
            .map { ToVideoModelUIMapper.map(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { recyclerLoadMoreProcess.value = true }
            .doOnNext { recyclerLoadMoreProcess.value = false }
            .doOnError { recyclerLoadMoreProcess.value = false }
            .subscribe({
                canLoadMoreLiveData.value = getAllVideosList.canLoadMore()
                videosLiveData.value = it
                error.value = null
            }, {
                error.value = it
            })
            .addTo(compositeDisposable)
    }

    private fun loadVideoDetails() {
        videoIdSubject.filter { it.isNotEmpty() }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap { id ->
                getVideoDetails.set(id)
                getVideoDetails.buildObservable()
            }
            .map { ToVideoDetailsModelUIMapper.map(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                videoDetails.value = it
            }, {
            })
            .addTo(compositeDisposable)
    }


    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun loadMoreItems() {
        loadMorePlaylist()
    }

    fun starClicked(videoItem: VideoUIModel) {
        if (videoItem.star) {
            deleteStarFromDb(videoItem.videoId)
        } else {
            saveStarToDb(videoItem.videoId)
        }
        showInAppReviewSubject.onNext(Any())
    }

    private fun saveStarToDb(videoId: String) {
        saveStarToDb.set(videoId)
        saveStarToDb.buildObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
            }, {
                videoItemStarChangedLiveData.value = videoId
            })
            .addTo(compositeDisposable)
    }

    private fun deleteStarFromDb(videoId: String) {
        deleteStarFromDb.set(videoId)
        deleteStarFromDb.buildObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
            }, {
                videoItemStarChangedLiveData.value = videoId
            })
            .addTo(compositeDisposable)
    }
}