package ru.goodibunakov.amlabvideo.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTODatabase

interface DatabaseRepository {
    /**
     * Вставить в БД лист плейлистов youtube-канала
     */
    fun insert(playlistsDTO: PlaylistsDTO): Completable

    /**
     * Удалить все записи в БД
     */
    fun delete(): Completable

    /**
     * Получение плейлистов из БД
     */
    fun getPlaylists(): Maybe<PlaylistsDTO>
}