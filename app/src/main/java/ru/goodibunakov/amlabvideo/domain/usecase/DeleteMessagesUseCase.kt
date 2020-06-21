package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.UseCase

class DeleteMessagesUseCase(private val databaseRepository: DatabaseRepository): UseCase<Unit, Unit>() {

    override fun buildObservable(): Observable<out Unit> {
        return databaseRepository.deleteAllNotifications()
                .toObservable()
    }
}