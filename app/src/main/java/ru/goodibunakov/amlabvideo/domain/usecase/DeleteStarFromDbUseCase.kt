package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.UseCase

class DeleteStarFromDbUseCase(private val databaseRepository: DatabaseRepository) : UseCase<String, Unit>() {

    private var videoId: String? = null

    override fun buildObservable(): Observable<out Unit> {
        return if (videoId != null) {
            databaseRepository.deleteStar(videoId!!)
                    .toObservable()
        } else {
            Observable.error(Throwable())
        }
    }

    override fun set(data: String) {
        videoId = data
    }
}