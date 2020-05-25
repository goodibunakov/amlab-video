package ru.goodibunakov.amlabvideo.data.repositories

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTODatabase
import ru.goodibunakov.amlabvideo.data.database.PlaylistsDao
import ru.goodibunakov.amlabvideo.data.mappers.ToDatabaseModelMapper
import ru.goodibunakov.amlabvideo.data.mappers.ToPlaylistsDTOMapper
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository

class DatabaseRepositoryImpl(private val playlistsDao: PlaylistsDao) : DatabaseRepository {

    override fun insert(playlistsDTO: PlaylistsDTO): Completable {
        Log.d("debug", "insert $playlistsDTO")
        Log.d("debug", "insert ${ToDatabaseModelMapper.map(playlistsDTO)}")
        return playlistsDao.insert(ToDatabaseModelMapper.map(playlistsDTO))
    }

    override fun delete(): Completable {
        return playlistsDao.delete()
    }

    override fun getPlaylists(): Maybe<PlaylistsDTO> {
        return playlistsDao.getPlaylists()
                .map { ToPlaylistsDTOMapper.map(it) }
                .doOnError { Log.d("debug", "database getPlaylists error = $it") }
                .doOnComplete { Log.d("debug", "database getPlaylists onComplete") }
    }
}