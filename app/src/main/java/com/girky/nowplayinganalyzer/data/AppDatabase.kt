package com.girky.nowplayinganalyzer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.girky.nowplayinganalyzer.data.converters.DateConverters
import com.girky.nowplayinganalyzer.data.dao.ListenHistoryDao
import com.girky.nowplayinganalyzer.data.model.ListenHistory

@Database(entities = [ListenHistory::class], version = 1, exportSchema = false)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun listenHistoryDao(): ListenHistoryDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "now_playing_db"
        ).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
