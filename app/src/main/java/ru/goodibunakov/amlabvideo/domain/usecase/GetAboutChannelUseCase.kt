package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.mappers.ToAboutChannelEntityMapper
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.UseCase
import ru.goodibunakov.amlabvideo.domain.entity.AboutChannelEntity

class GetAboutChannelUseCase(private val apiRepository: ApiRepository): UseCase<Unit, AboutChannelEntity>() {

    override fun buildObservable(): Observable<out AboutChannelEntity> {
        return apiRepository.getChannelDetails()
                .map { ToAboutChannelEntityMapper.map(it) }
    }
}