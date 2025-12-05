package com.girky.nowplayinganalyzer.ui.history

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.girky.nowplayinganalyzer.data.AppDatabase
import com.girky.nowplayinganalyzer.data.model.ListenHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

  private val dao = AppDatabase.getDatabase(application).listenHistoryDao()

  // 権限状態
  private val _hasNotificationPermission = MutableStateFlow(false)
  val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission

  // 履歴データ (日付でグループ化)
  val historyGroups: StateFlow<Map<LocalDate, List<ListenHistory>>> = dao.getAll()
    .map { list ->
      list.groupBy { it.timestamp.toLocalDate() }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyMap()
    )

  fun checkPermission() {
    val context = getApplication<Application>()
    val packageName = context.packageName
    val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    val enabled = flat != null && flat.contains(packageName)
    _hasNotificationPermission.value = enabled
  }

  companion object {
    val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: androidx.lifecycle.viewmodel.CreationExtras
      ): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        return HistoryViewModel(application) as T
      }
    }
  }
}
