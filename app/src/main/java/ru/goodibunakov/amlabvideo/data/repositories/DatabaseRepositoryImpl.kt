package ru.goodibunakov.amlabvideo.data.repositories

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.data.database.Dao
import ru.goodibunakov.amlabvideo.data.mappers.ToDatabaseModelMapper
import ru.goodibunakov.amlabvideo.data.mappers.ToPlaylistsDTOMapper
import ru.goodibunakov.amlabvideo.data.model.MessageItem
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import java.util.*

class DatabaseRepositoryImpl(private val dao: Dao, private val context: Context) : DatabaseRepository {

    override fun insertPlaylists(playlistsDTO: PlaylistsDTO): Completable {
        return dao.insertPlaylists(ToDatabaseModelMapper.map(playlistsDTO))
    }

    override fun deletePlaylists(): Completable {
        return dao.deletePlaylists()
                .doOnComplete { Log.d("debug", "DatabaseRepositoryImpl deletePlaylists oncomplete") }
                .doOnError { Log.d("debug", "DatabaseRepositoryImpl deletePlaylists error = $it") }
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

    override fun saveNotification(messageItem: MessageItem): Completable {
        Glide.with(context).downloadOnly().load(messageItem.image).submit().get()
        return dao.saveNotification(messageItem)
    }

    override fun getAllNotifications(): Observable<List<MessageItem>> {
        return dao.getAllNotifications()
    }

    override fun deleteAllNotifications(): Completable {
        return dao.deleteAllNotifications()
    }
}