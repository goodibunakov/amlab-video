package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.model.VideoItemModel
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.UseCase

class SaveStarToDbUseCase(
        private val apiRepository: ApiRepository,
        private val databaseRepository: DatabaseRepository) : UseCase<String, Unit>() {

    private var videoId: String? = null

    override fun buildObservable(): Observable<out Unit> {
        var itemById: VideoItemModel? = null
        videoId?.let {
            itemById = apiRepository.getItemById(it)
        }
        return if (itemById != null) {
            databaseRepository.saveStar(itemById!!)
                    .toObservable()
        } else {
            Observable.error(Throwable("No such item"))
        }
    }

    override fun set(data: String) {
        videoId = data
    }
}