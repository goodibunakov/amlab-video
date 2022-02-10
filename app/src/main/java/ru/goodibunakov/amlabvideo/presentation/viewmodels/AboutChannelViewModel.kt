package ru.goodibunakov.amlabvideo.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.domain.usecase.GetAboutChannelUseCase
import ru.goodibunakov.amlabvideo.presentation.mappers.ToAboutChannelUIMapper
import ru.goodibunakov.amlabvideo.presentation.model.AboutChannelUIModel

class AboutChannelViewModel(getAboutChannelUseCase: GetAboutChannelUseCase) : ViewModel() {

    val liveData = MutableLiveData<AboutChannelUIModel?>()
    val progressBarVisibilityLiveData = MutableLiveData<Boolean>()
    val error = MutableLiveData<Throwable?>().apply { this.value = null }

    private var disposable: Disposable = getAboutChannelUseCase.buildObservable()
        .subscribeOn(Schedulers.io())
        .map { ToAboutChannelUIMapper.map(it) }
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe {
            progressBarVisibilityLiveData.value = true
        }
        .doOnError {
            progressBarVisibilityLiveData.value = false
        }
        .doOnNext {
            progressBarVisibilityLiveData.value = false
        }
        .subscribe({
            liveData.value = it
            error.value = null
        }, {
            error.value = it
        })

    override fun onCleared() {
        if (!disposable.isDisposed) disposable.dispose()
        super.onCleared()
    }
}