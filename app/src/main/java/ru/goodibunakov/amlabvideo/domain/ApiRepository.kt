package ru.goodibunakov.amlabvideo.domain

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.goodibunakov.amlabvideo.api.dto.channel_details.BrandingDTO
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.video.VideoDTO
import ru.goodibunakov.amlabvideo.api.dto.video_details.VideoDetailsDTO
import ru.goodibunakov.amlabvideo.api.dto.videos_all.AllVideosDTO
import ru.goodibunakov.amlabvideo.data.model.VideoItemModel
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus

interface ApiRepository {
    /**
     * Получение всех видео на канале
     */
    fun getAllVideosList(): Observable<AllVideosDTO>

    /**
     * Получение всех видео на канале
     */
    fun getMoreAllVideosList(pageToken: String): Observable<AllVideosDTO>

    /**
     * Получение списка плейлистов
     */
    fun getPlayLists(): Observable<PlaylistsDTO>

    /**
     * Получение плейтиста
     */
    fun getPlaylistVideos(playlistId: String, pageToken: String? = ""): Observable<VideoDTO>

    /**
     * Получение всей информации об одном видео
     */
    fun getVideoDetails(id: String): Observable<VideoDetailsDTO>

    /**
     * Наличие интернета
     */
    fun networkConnected(): BehaviorSubject<ConnectedStatus>

    /**
     * Инфо о канале
     */
    fun getChannelDetails(): Observable<BrandingDTO>

    /**
     * Очистить лист при установке нового playlistId
     */
    fun clearPlaylistItems()

    /**
     * Получить snippet из playlistItems по id
     */
    fun getItemById(videoId: String): VideoItemModel?
}