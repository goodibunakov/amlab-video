package ru.goodibunakov.amlabvideo.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants

@Entity(
    tableName = DatabaseConstants.TABLE_STAR,
    indices = [Index(value = ["videoId"], unique = true)]
)
data class VideoItemModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val videoId: String,
    val playlistId: String,
    val imageUrl: String?,
    val createdDate: String,
    val star: Boolean
)