package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.mappers.ToVideoEntityMapperForAllVideosList
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.UseCase
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity

class GetAllVideosListUseCase(private val apiRepository: ApiRepository) : UseCase<String?, List<VideoEntity>>() {

    private var pageToken: String? = ""

    override fun buildObservable(): Observable<out List<VideoEntity>> {
        return apiRepository.getAllVideosList()
                .doOnNext {
                    set(it.nextPageToken)
                }
                .map { ToVideoEntityMapperForAllVideosList.map(it) }
    }

    fun getMoreAllVideosList(): Observable<out List<VideoEntity>> {
        return apiRepository.getMoreAllVideosList(pageToken!!)
                    .map { ToVideoEntityMapperForAllVideosList.map(it) }
    }

    override fun set(data: String?) {
        pageToken = data
    }

    fun canLoadMore(): Boolean {
        return !pageToken.isNullOrEmpty()
    }
}