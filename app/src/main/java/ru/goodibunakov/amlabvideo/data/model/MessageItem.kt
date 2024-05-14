package ru.goodibunakov.amlabvideo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_MESSAGES)
data class MessageItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val body: String,
    val image: String,
    val launchURL: String?,
    val dateReceived: Long
)