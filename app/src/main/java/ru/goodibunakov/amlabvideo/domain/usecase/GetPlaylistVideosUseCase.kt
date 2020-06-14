package ru.goodibunakov.amlabvideo.domain.usecase

import android.util.Log
import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.mappers.ToVideoEntityMapper
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.UseCase
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity

class GetPlaylistVideosUseCase(private val apiRepository: ApiRepository) : UseCase<String, List<VideoEntity>>() {

    private var playlistId: String = ""
    private var pageToken : String? = ""

    override fun buildObservable(): Observable<out List<VideoEntity>> {
        Log.d("debug", "playlistId = $playlistId")
        return apiRepository.getPlaylistVideos(playlistId = playlistId)
                .doOnNext {
                    pageToken = it.nextPageToken
                    Log.d("ddd", "pageToken1 = $pageToken")
                }
                .map { ToVideoEntityMapper.map(it) }
    }

    fun loadMore(): Observable<out List<VideoEntity>> {
        return apiRepository.getPlaylistVideos(playlistId = playlistId, pageToken = pageToken)
                .doOnNext {
                    pageToken = it.nextPageToken
                    Log.d("ddd", "pageToken2 = $pageToken")
                }
                .map { ToVideoEntityMapper.map(it) }
    }

    override fun set(data: String) {
        playlistId = data
    }

    fun canLoadMore(): Boolean {
        return pageToken != null
    }
}