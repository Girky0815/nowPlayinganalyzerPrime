package com.girky.nowplayinganalyzer.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.girky.nowplayinganalyzer.data.AppDatabase
import com.girky.nowplayinganalyzer.data.model.ListenHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NowPlayingListenerService : NotificationListenerService() {

  private val job = SupervisorJob()
  private val scope = CoroutineScope(Dispatchers.IO + job)

  override fun onListenerConnected() {
    super.onListenerConnected()
    Log.i(TAG, "NowPlayingListenerService connected")
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    super.onNotificationPosted(sbn)
    if (sbn == null) return

    val packageName = sbn.packageName
    Log.v(TAG, "onNotificationPosted: $packageName")

    // Pixel Ambient Services / Android System Intelligence のパッケージ名
    // com.google.intelligence.sense (Old)
    // com.google.android.as (New)
    if (packageName != "com.google.intelligence.sense" && packageName != "com.google.android.as") {
      return
    }

    val extras = sbn.notification.extras
    val titleRaw = extras.getString(Notification.EXTRA_TITLE) ?: ""
    val textRaw = extras.getString(Notification.EXTRA_TEXT) ?: ""

    Log.i(TAG, "Target notification received from $packageName: Title=$titleRaw, Text=$textRaw")

    if (titleRaw.isNotBlank()) {
      var songTitle = titleRaw
      var artistName = textRaw

      // パターン検出: "曲名 (アーティスト名)" または "曲名（アーティスト名）"
      // 全角・半角スペース、全角・半角括弧に対応
      val pattern = Regex("""^(.*)[\s　]*[（(](.*)[)）]$""")
      val matchResult = pattern.find(titleRaw)

      if (matchResult != null) {
        val (t, a) = matchResult.destructured
        songTitle = t.trim()
        artistName = a.trim()
        Log.d(TAG, "Parsed Notification: Title='$songTitle', Artist='$artistName'")
      } else {
        // パターンにマッチしない場合
        // textRaw が "タップで..." などのシステムメッセージの場合はアーティスト名として不適切なので無視または不明とする
        if (textRaw.contains("タップ") || textRaw.contains("履歴")) {
           artistName = "Unknown Artist"
        }
      }

      saveListenHistory(songTitle, artistName)
    }
  }

  private fun saveListenHistory(title: String, artist: String) {
    scope.launch {
      try {
        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.listenHistoryDao()
        val now = LocalDateTime.now()

        // 重複チェック: 過去10分以内に同じ曲が記録されていればスキップ
        val tenMinutesAgo = now.minusMinutes(10)
        val recent = dao.findRecent(title, artist, tenMinutesAgo)

        if (recent != null) {
          Log.d(TAG, "Duplicate detected: $title / $artist (Calculated since $tenMinutesAgo)")
          return@launch
        }

        val history = ListenHistory(
          title = title,
          artist = artist,
          timestamp = now
        )
        dao.insert(history)
        Log.i(TAG, "Saved: $title / $artist at $now")

      } catch (e: Exception) {
        Log.e(TAG, "Error saving history", e)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    job.cancel()
  }

  companion object {
    private const val TAG = "NowPlayingListener"
  }
}
