package ru.goodibunakov.amlabvideo.presentation.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.domain.usecase.GetAboutChannelUseCase
import ru.goodibunakov.amlabvideo.presentation.mappers.ToAboutChannelUIMapper
import ru.goodibunakov.amlabvideo.presentation.model.AboutChannelUIModel

class AboutChannelViewModel(getAboutChannelUseCase: GetAboutChannelUseCase) : ViewModel() {

    val liveData = MutableLiveData<AboutChannelUIModel>()
    val progressBarVisibilityLiveData = MutableLiveData<Int>()

    private var disposable: Disposable = getAboutChannelUseCase.buildObservable()
            .subscribeOn(Schedulers.io())
            .map { ToAboutChannelUIMapper.map(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                progressBarVisibilityLiveData.value = View.VISIBLE
            }
            .doOnError {
                progressBarVisibilityLiveData.value = View.GONE
            }
            .doOnNext {
                progressBarVisibilityLiveData.value = View.GONE
            }
            .subscribe({
                liveData.value = it
            }, {})

    override fun onCleared() {
        if (!disposable.isDisposed) disposable.dispose()
        super.onCleared()
    }
}