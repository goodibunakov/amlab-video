package ru.goodibunakov.amlabvideo.data.database

import androidx.room.*
import androidx.room.Dao
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants.COLUMN_DATE
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants.TABLE_MESSAGES
import ru.goodibunakov.amlabvideo.data.model.PlaylistsDTODatabase
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants.TABLE_PLAYLISTS
import ru.goodibunakov.amlabvideo.data.model.MessageItem

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylists(playlists: PlaylistsDTODatabase): Completable

    @Query("DELETE FROM $TABLE_PLAYLISTS")
    fun deletePlaylists(): Completable

    @Update(entity = PlaylistsDTODatabase::class)
    fun updatePlaylists(playlists: PlaylistsDTODatabase): Maybe<Int>

    @Query("SELECT * FROM $TABLE_PLAYLISTS")
    fun getPlaylists(): Maybe<PlaylistsDTODatabase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveNotification(messageItem: MessageItem): Completable

    @Query("DELETE FROM $TABLE_MESSAGES")
    fun deleteAllNotifications(): Completable

    @Query("SELECT * FROM $TABLE_MESSAGES ORDER BY $COLUMN_DATE DESC")
    fun getAllNotifications(): Observable<List<MessageItem>>
}