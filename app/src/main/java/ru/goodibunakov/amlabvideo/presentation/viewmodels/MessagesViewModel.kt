package ru.goodibunakov.amlabvideo.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.domain.usecase.DeleteMessagesUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetMessagesUseCase
import ru.goodibunakov.amlabvideo.presentation.mappers.ToMessageUIItemMapper
import ru.goodibunakov.amlabvideo.presentation.model.MessageUIItem

class MessagesViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val deleteMessagesUseCase: DeleteMessagesUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    val messagesLiveData = MutableLiveData<List<MessageUIItem>>()
    val emptyMessagesLiveData = MutableLiveData(false)
    val errorDeleteMessagesLiveData = MutableLiveData<Throwable?>()
    val errorGetMessagesLiveData = MutableLiveData<Throwable?>(null)
    val progressbarLiveData = MutableLiveData<Boolean>()

    init {
        getAllMessages()
    }


    private fun getAllMessages() {
        getMessagesUseCase.buildObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap { list ->
                Observable.fromIterable(list)
                    .map { item -> ToMessageUIItemMapper.map(item) }
                    .toList()
                    .toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progressbarLiveData.value = true }
            .doOnNext { progressbarLiveData.value = false }
            .doOnError { progressbarLiveData.value = false }
            .subscribe({
                Log.d("debug", "MessagesViewModel getAllMessages onNext = $it")
                messagesLiveData.value = it
                errorGetMessagesLiveData.value = null
                emptyMessagesLiveData.value = it.isEmpty()
            }, {
                Log.d("debug", "MessagesViewModel getAllMessages error = $it")
                errorGetMessagesLiveData.value = it
                emptyMessagesLiveData.value = false
            }, {
                Log.d("debug", "MessagesViewModel getAllMessages onComplete")
            })
            .addTo(compositeDisposable)
    }

    fun deleteAllMessages() {
        deleteMessagesUseCase.buildObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progressbarLiveData.value = true }
            .doFinally { progressbarLiveData.value = false }
            .subscribe({
//                    messagesLiveData.value = emptyList()
                errorDeleteMessagesLiveData.value = null
                Log.d("debug", "MessagesViewModel deleteAllMessages deleted!")
            }, {
                Log.d("debug", "MessagesViewModel deleteAllMessages error = $it")
                errorDeleteMessagesLiveData.value = it
            })
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}