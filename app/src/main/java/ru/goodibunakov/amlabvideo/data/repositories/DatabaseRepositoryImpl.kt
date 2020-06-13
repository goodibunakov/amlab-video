package ru.goodibunakov.amlabvideo.data.repositories

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Maybe
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTODatabase
import ru.goodibunakov.amlabvideo.data.database.Dao
import ru.goodibunakov.amlabvideo.data.mappers.ToDatabaseModelMapper
import ru.goodibunakov.amlabvideo.data.mappers.ToPlaylistsDTOMapper
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository

class DatabaseRepositoryImpl(private val dao: Dao) : DatabaseRepository {

    override fun insertPlaylists(playlistsDTO: PlaylistsDTO): Completable {
        return dao.insertPlaylists(ToDatabaseModelMapper.map(playlistsDTO))
//        return deletePlaylists()
//                .andThen { Completable.defer { dao.insertPlaylists(ToDatabaseModelMapper.map(playlistsDTO))
//                        .doOnComplete { Log.d("debug", "DatabaseRepositoryImpl insertPlaylists oncomplete") }
//                        .doOnError { Log.d("debug", "DatabaseRepositoryImpl insertPlaylists error = $it")  }}}
    }

    override fun deletePlaylists(): Completable {
        return dao.deletePlaylists()
                .doOnComplete { Log.d("debug", "DatabaseRepositoryImpl deletePlaylists oncomplete") }
                .doOnError { Log.d("debug", "DatabaseRepositoryImpl deletePlaylists error = $it")  }
    }

    override fun getPlaylists(): Maybe<PlaylistsDTO> {
        return dao.getPlaylists()
                .map { ToPlaylistsDTOMapper.map(it) }
                .doOnError { Log.d("debug", "database getPlaylists error = $it") }
                .doOnComplete { Log.d("debug", "database getPlaylists onComplete") }
    }

    override fun updatePlaylists(playlistsDTO: PlaylistsDTO): Maybe<Int> {
        return dao.updatePlaylists(ToDatabaseModelMapper.map(playlistsDTO))
    }
}