package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.mappers.ToVideoEntityFromVideoItemModelMapper
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.UseCase
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity

class GetStarsFromDbUseCase(private val databaseRepository: DatabaseRepository) : UseCase<Unit, List<VideoEntity>>() {

    override fun buildObservable(): Observable<out List<VideoEntity>> {
        return databaseRepository.getStars()
                .map { ToVideoEntityFromVideoItemModelMapper.map(it) }
                .toObservable()
    }
}