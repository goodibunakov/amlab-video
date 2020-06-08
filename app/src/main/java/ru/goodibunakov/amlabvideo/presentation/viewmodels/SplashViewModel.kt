package ru.goodibunakov.amlabvideo.presentation.viewmodels

import io.reactivex.disposables.CompositeDisposable
import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase

class SplashViewModel(
        getChannelPlaylistsUseCase: GetChannelPlaylistsUseCase,
        getNetworkStatus: GetNetworkStatusUseCase
) : BaseActivityViewModel(getNetworkStatus, getChannelPlaylistsUseCase) {

    private var compositeDisposable = CompositeDisposable()
//    val playlistsLiveData = MutableLiveData<List<PlaylistsModelUI>>()
//    val error = SingleLiveEvent<Boolean>().apply { this.value = false }


    init {
//        getChannelPlaylistsUseCase.buildObservable()
//                .map { ToPlaylistsModelUIMapper.map(it) }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    Log.d("ddd", "getChannelPlaylistsUseCase = $it")
//                    playlistsLiveData.value = it
//                    error.value = false
//                }, {
//                    error.value = true
//                    Log.d("ddd", "getChannelPlaylistsUseCase error = ${it.localizedMessage}, ${it.cause}")
//                }, {
//                    Log.d("ddd", "getChannelPlaylistsUseCase complete")
//                })
//                .addTo(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}