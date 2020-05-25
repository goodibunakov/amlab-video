package ru.goodibunakov.amlabvideo.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTODatabase
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants.TABLE

@Dao
interface PlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlists: PlaylistsDTODatabase): Completable

    @Query("DELETE FROM $TABLE")
    fun delete(): Completable

    @Query("SELECT * FROM $TABLE")
    fun getPlaylists(): Maybe<PlaylistsDTODatabase>
}