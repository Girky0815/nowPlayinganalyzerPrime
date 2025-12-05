package com.girky.nowplayinganalyzer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.girky.nowplayinganalyzer.data.model.ListenHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ListenHistoryDao {
  @Insert
  suspend fun insert(history: ListenHistory)

  // 全履歴を日付降順で取得
  @Query("SELECT * FROM listen_history ORDER BY timestamp DESC")
  fun getAll(): Flow<List<ListenHistory>>

  // 重複チェック用：指定された時間以降の特定の曲を取得
  @Query("""
    SELECT * FROM listen_history 
    WHERE title = :title 
      AND artist = :artist 
      AND timestamp > :since
    ORDER BY timestamp DESC 
    LIMIT 1
  """)
  suspend fun findRecent(title: String, artist: String, since: LocalDateTime): ListenHistory?
}
