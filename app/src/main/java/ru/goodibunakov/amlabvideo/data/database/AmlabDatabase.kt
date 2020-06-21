package ru.goodibunakov.amlabvideo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.goodibunakov.amlabvideo.data.model.PlaylistsDTODatabase
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants.DATABASE_NAME
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants.DATABASE_VERSION
import ru.goodibunakov.amlabvideo.data.model.MessageItem
import ru.goodibunakov.amlabvideo.data.model.VideoItemModel

@Database(entities = [PlaylistsDTODatabase::class, MessageItem::class, VideoItemModel::class], version = DATABASE_VERSION, exportSchema = true)
abstract class AmlabDatabase : RoomDatabase() {

    abstract fun dao(): Dao

    companion object {

        @Volatile
        private var INSTANCE: AmlabDatabase? = null

        fun getDatabase(context: Context): AmlabDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AmlabDatabase::class.java,
                        DATABASE_NAME).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}