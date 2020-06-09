package ru.goodibunakov.amlabvideo.data.repositories

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Maybe
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.data.database.Dao
import ru.goodibunakov.amlabvideo.data.mappers.ToDatabaseModelMapper
import ru.goodibunakov.amlabvideo.data.mappers.ToPlaylistsDTOMapper
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository

class DatabaseRepositoryImpl(private val dao: Dao) : DatabaseRepository {

    override fun insertPlaylists(playlistsDTO: PlaylistsDTO): Completable {
        Log.d("debug", "insert $playlistsDTO")
        Log.d("debug", "insert ${ToDatabaseModelMapper.map(playlistsDTO)}")
        return dao.insertPlaylists(ToDatabaseModelMapper.map(playlistsDTO))
    }

    override fun deletePlaylists(): Completable {
        return dao.deletePlaylists()
    }

    override fun getPlaylists(): Maybe<PlaylistsDTO> {
        return dao.getPlaylists()
                .map { ToPlaylistsDTOMapper.map(it) }
                .doOnError { Log.d("debug", "database getPlaylists error = $it") }
                .doOnComplete { Log.d("debug", "database getPlaylists onComplete") }
    }
}