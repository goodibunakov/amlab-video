package ru.goodibunakov.amlabvideo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTODatabase
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants.DATABASE_NAME
import ru.goodibunakov.amlabvideo.data.database.DatabaseConstants.DATABASE_VERSION

@Database(entities = [PlaylistsDTODatabase::class], version = DATABASE_VERSION, exportSchema = true)
abstract class PlaylistsDatabase : RoomDatabase() {

    abstract fun playlistsDao(): PlaylistsDao

    companion object {

        @Volatile
        private var INSTANCE: PlaylistsDatabase? = null

        fun getDatabase(context: Context): PlaylistsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlaylistsDatabase::class.java,
                        DATABASE_NAME).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}