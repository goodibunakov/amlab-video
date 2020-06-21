package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.mappers.ToMessageEntityMapper
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.UseCase
import ru.goodibunakov.amlabvideo.domain.entity.MessageEntity

class GetMessagesUseCase(private val databaseRepository: DatabaseRepository) : UseCase<Unit, MutableList<MessageEntity>>() {

    override fun buildObservable(): Observable<out MutableList<MessageEntity>> {
        return databaseRepository.getAllNotifications()
                .flatMap { list ->
                    Observable.fromIterable(list)
                            .map { item -> ToMessageEntityMapper.map(item) }
                            .toList()
                            .toObservable()
                }
    }
}