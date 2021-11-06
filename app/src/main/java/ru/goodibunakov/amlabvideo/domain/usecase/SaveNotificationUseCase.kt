package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.model.MessageItem
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.UseCase

class SaveNotificationUseCase(
    private val databaseRepository: DatabaseRepository
) : UseCase<MessageItem, Unit>() {

    private var message: MessageItem? = null

    override fun buildObservable(): Observable<out Unit> {
        return if (message == null)
            Observable.empty()
        else {
            databaseRepository.saveNotification(message!!).toObservable()
        }
    }

    override fun set(data: MessageItem) {
        message = data
    }
}