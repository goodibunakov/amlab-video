package ru.goodibunakov.amlabvideo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_PLAYLISTS)
data class PlaylistsDTODatabase(
    @PrimaryKey
    val id: Int = 0,
    val playlistsDTOjson: String
)