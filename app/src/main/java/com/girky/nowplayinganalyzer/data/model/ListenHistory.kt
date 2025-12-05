package com.girky.nowplayinganalyzer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 音楽検知履歴エンティティ
 */
@Entity(tableName = "listen_history")
data class ListenHistory(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  
  val title: String,
  val artist: String,
  
  // 検知日時 (保存時に現在時刻を設定)
  val timestamp: LocalDateTime = LocalDateTime.now(),
  
  // どのモデルで分析されたかなどのメタデータ用 (将来拡張)
  val analyzedBy: String? = null
)
