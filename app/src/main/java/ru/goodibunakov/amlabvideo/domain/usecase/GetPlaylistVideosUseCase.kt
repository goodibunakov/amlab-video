package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.mappers.ToVideoEntityMapper
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.UseCase
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity

class GetPlaylistVideosUseCase(
        private val apiRepository: ApiRepository
) : UseCase<String, List<VideoEntity>>() {

    private var playlistId: String = ""

    override fun buildObservable(): Observable<out List<VideoEntity>> {
        return apiRepository.getPlaylistVideos(playlistId)
                .map { ToVideoEntityMapper.map(it) }
    }

    override fun set(data: String) {
        playlistId = data
    }
}