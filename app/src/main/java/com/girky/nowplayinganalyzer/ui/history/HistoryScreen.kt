package com.girky.nowplayinganalyzer.ui.history

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import com.girky.nowplayinganalyzer.ui.components.GroupedListItem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.girky.nowplayinganalyzer.data.model.ListenHistory
import com.girky.nowplayinganalyzer.ui.theme.AppFontFamilyEmphasized
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HistoryScreen(
  viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  // 画面表示時に権限チェックを行う
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        viewModel.checkPermission()
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  val hasPermission by viewModel.hasNotificationPermission.collectAsState()
  val historyGroups by viewModel.historyGroups.collectAsState()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 権限警告カード
      if (!hasPermission) {
        PermissionWarningCard(
          onClick = {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            context.startActivity(intent)
          }
        )
      }

      // 履歴リスト
      if (historyGroups.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "履歴がありません", style = MaterialTheme.typography.bodyLarge)
        }
      } else {
        HistoryList(historyGroups = historyGroups)
      }
    }
  }
}

@Composable
fun PermissionWarningCard(onClick: () -> Unit) {
  Card(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.errorContainer,
      contentColor = MaterialTheme.colorScheme.onErrorContainer
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(Icons.Filled.Warning, contentDescription = "Warning")
      Spacer(modifier = Modifier.padding(8.dp))
      Column {
        Text(text = "通知へのアクセスが必要です", fontWeight = FontWeight.Bold)
        Text(text = "タップして設定画面で許可してください")
      }
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HistoryList(historyGroups: Map<LocalDate, List<ListenHistory>>) {
  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp) // 要求仕様: アイテム間 2dp
  ) {
    historyGroups.forEach { (date, items) ->
      item {
        // グループ間の間隔調整 (前のグループとの間を開ける)
        Spacer(modifier = Modifier.height(16.dp))
        DateHeader(date = date)
        Spacer(modifier = Modifier.height(8.dp))
      }
      
      itemsIndexed(items) { index, history ->
        GroupedListItem(
            index = index, 
            totalCount = items.size,
            onClick = { /* 詳細表示などは今後実装 */ }
        ) {
            HistoryItemContent(history = history)
        }
      }
    }
  }
}

@Composable
fun DateHeader(date: LocalDate) {
  val formatter = DateTimeFormatter.ofPattern("M月d日 (E)", Locale.JAPAN)
  Text(
    text = date.format(formatter),
    style = MaterialTheme.typography.titleMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp)
  )
}

/**
 * GroupedListItem の内部コンテンツ
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HistoryItemContent(history: ListenHistory) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Text(
      text = history.title,
      style = MaterialTheme.typography.titleMedium.copy(
        fontFamily = AppFontFamilyEmphasized,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
          text = history.artist,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        Text(
          text = history.timestamp.format(timeFormatter),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
    }
  }
}
