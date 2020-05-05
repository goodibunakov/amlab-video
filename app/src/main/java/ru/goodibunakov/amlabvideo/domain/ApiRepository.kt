package ru.goodibunakov.amlabvideo.domain

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.videos.AllVideosDTO

interface ApiRepository {
    /**
     * Получение всех видео на канале Amlab
     */
    fun getAllVideosList(): Observable<AllVideosDTO>

    /**
     * Получение списка плейлистов
     */
    fun getPlayLists(): Observable<PlaylistsDTO>
}