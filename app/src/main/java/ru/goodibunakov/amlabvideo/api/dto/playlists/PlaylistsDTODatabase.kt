package ru.goodibunakov.amlabvideo.api.dto.playlists

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE)
data class PlaylistsDTODatabase(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val playlistsDTOjson: String
)