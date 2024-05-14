package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.api.dto.video_details.VideoDetailsDTO
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.UseCase

class GetVideoDetailsUseCase(
    private val apiRepository: ApiRepository
) : UseCase<String, VideoDetailsDTO>() {

    private var videoId = ""

    override fun buildObservable(): Observable<out VideoDetailsDTO> {
        return apiRepository.getVideoDetails(videoId)
    }

    override fun set(data: String) {
        videoId = data
    }
}