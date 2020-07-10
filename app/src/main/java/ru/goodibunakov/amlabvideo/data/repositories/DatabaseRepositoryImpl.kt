package ru.goodibunakov.amlabvideo.data.repositories

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.data.database.Dao
import ru.goodibunakov.amlabvideo.data.mappers.ToDatabaseModelMapper
import ru.goodibunakov.amlabvideo.data.mappers.ToPlaylistsDTOMapper
import ru.goodibunakov.amlabvideo.data.model.MessageItem
import ru.goodibunakov.amlabvideo.data.model.VideoItemModel
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository

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
        if (messageItem.image.isNotEmpty()) Glide.with(context).downloadOnly().load(messageItem.image).submit().get()
        return dao.saveNotification(messageItem)
    }

    override fun getAllNotifications(): Observable<List<MessageItem>> {
        return dao.getAllNotifications()
    }

    override fun deleteAllNotifications(): Completable {
        return dao.deleteAllNotifications()
    }

    override fun saveStar(videoItemModel: VideoItemModel): Completable {
        return dao.saveStar(videoItemModel)
                .doOnSubscribe { Log.d("debug", "DatabaseRepositoryImpl saveStar OnSubscribe") }
                .doOnComplete { Log.d("debug", "DatabaseRepositoryImpl saveStar onComplete") }
                .doOnError { Log.d("debug", "DatabaseRepositoryImpl saveStar onError = $it") }
    }

    override fun deleteStar(videoId: String): Completable {
        return dao.deleteStar(videoId)
    }

    override fun getStars(): Single<List<VideoItemModel>> {
        return dao.getStars()
                .doOnSubscribe { Log.d("debug", "DatabaseRepositoryImpl getStars OnSubscribe") }
                .doOnSuccess { Log.d("debug", "DatabaseRepositoryImpl getStars onSuccess") }
                .doOnError { Log.d("debug", "DatabaseRepositoryImpl getStars onError = $it") }
    }

    override fun getStarIds(playlistId: String): Observable<List<String>> {
        return dao.getStarIds(playlistId)
    }
}