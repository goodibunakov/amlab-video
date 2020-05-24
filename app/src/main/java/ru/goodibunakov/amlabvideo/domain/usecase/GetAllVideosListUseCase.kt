package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.mappers.ToVideoEntityMapperForAllVideosList
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.UseCase
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity

class GetAllVideosListUseCase(private val apiRepository: ApiRepository) : UseCase<Unit, List<VideoEntity>>() {

    override fun buildObservable(): Observable<out List<VideoEntity>> {
        return apiRepository.getAllVideosList()
                .map { ToVideoEntityMapperForAllVideosList.map(it) }
    }
}