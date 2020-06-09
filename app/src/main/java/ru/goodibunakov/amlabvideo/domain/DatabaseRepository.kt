package ru.goodibunakov.amlabvideo.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO

interface DatabaseRepository {
    /**
     * Вставить в БД лист плейлистов youtube-канала
     */
    fun insertPlaylists(playlistsDTO: PlaylistsDTO): Completable

    /**
     * Удалить все плейлисты в БД
     */
    fun deletePlaylists(): Completable

    /**
     * Получение плейлистов из БД
     */
    fun getPlaylists(): Maybe<PlaylistsDTO>
}