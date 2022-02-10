package ru.goodibunakov.amlabvideo.domain.usecase

import android.util.Log
import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.mappers.ToVideoEntityMapper
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.UseCase
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity

class GetPlaylistVideosUseCase(
    private val apiRepository: ApiRepository,
    private val databaseRepository: DatabaseRepository
) : UseCase<String, List<VideoEntity>>() {

    private var playlistId: String = ""
    private var pageToken: String? = ""


    override fun buildObservable(): Observable<out List<VideoEntity>> {
        Log.d("debug", "playlistId = $playlistId")
        return apiRepository.getPlaylistVideos(playlistId = playlistId, pageToken = pageToken)
            .doOnNext {
                pageToken = it.nextPageToken
                Log.d("ddd", "pageToken1 = $pageToken")
            }
            .map { ToVideoEntityMapper.map(it) }
            .map { it.distinct() }
            .map {
                it.filter { videoEntity ->
                    videoEntity.title != "Private video"
                }
            }
            .flatMap { setStars(it) }
    }

    private fun setStars(list: List<VideoEntity>): Observable<List<VideoEntity>> {
        return databaseRepository.getStarIds(playlistId)
            .firstElement()
            .toObservable()
            .map { ids ->
                if (ids.isEmpty()) {
                    Log.d(
                        "debug",
                        "GetPlaylistVideosUseCase setStars ids = $ids возвращаем исходный лист"
                    )
                    return@map list
                }
                Log.d("debug", "GetPlaylistVideosUseCase setStars ids = $ids")
                return@map list.map {
                    if (ids.contains(it.videoId))
                        it.copy(star = true)
                    else it
                }
            }
    }

    override fun set(data: String) {
        apiRepository.clearPlaylistItems()
        pageToken = ""
        playlistId = data
    }

    fun canLoadMore(): Boolean {
        return pageToken != null
    }
}