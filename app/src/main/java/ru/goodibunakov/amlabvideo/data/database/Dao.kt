package ru.goodibunakov.amlabvideo.data.database

import androidx.room.*
import androidx.room.Dao
import io.reactivex.Completable
import io.reactivex.Maybe
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTODatabase
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants.TABLE_PLAYLISTS

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
}