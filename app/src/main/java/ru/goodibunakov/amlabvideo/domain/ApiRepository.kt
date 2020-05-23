package ru.goodibunakov.amlabvideo.domain

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.video.VideoDTO
import ru.goodibunakov.amlabvideo.api.dto.videos.AllVideosDTO
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus

interface ApiRepository {
    /**
     * Получение всех видео на канале Amlab
     */
    fun getAllVideosList(): Observable<AllVideosDTO>

    /**
     * Получение списка плейлистов
     */
    fun getPlayLists(): Observable<PlaylistsDTO>

    /**
     * Получение плейтиста
     */
    fun getPlaylistVideos(playlistId: String): Observable<VideoDTO>

    fun networkConnected(): BehaviorSubject<ConnectedStatus>
}