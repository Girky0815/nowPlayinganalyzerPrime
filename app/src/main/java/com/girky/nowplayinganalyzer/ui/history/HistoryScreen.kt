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
import androidx.compose.material.icons.Icons
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
    containerColor = MaterialTheme.colorScheme.background // Theme.kt で設定した SurfaceContainerHigh が反映されるはず
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
    contentPadding = PaddingValues(bottom = 16.dp)
  ) {
    historyGroups.forEach { (date, items) ->
      item {
        DateHeader(date = date)
      }
      items(items) { history ->
        HistoryItem(history = history)
      }
    }
  }
}

@Composable
fun DateHeader(date: LocalDate) {
  val formatter = DateTimeFormatter.ofPattern("MK月d日 (E)", Locale.JAPAN)
  Text(
    text = date.format(formatter),
    style = MaterialTheme.typography.titleMedium,
    color = MaterialTheme.colorScheme.primary,
    modifier = Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
      .padding(horizontal = 16.dp, vertical = 8.dp)
  )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HistoryItem(history: ListenHistory) {
  // M3E ぽさを出すために SurfaceBright のような色を使いたいが、
  // Theme.kt の設定次第。ここでは Card を使用。
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant // 明るめの色想定
    )
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = history.title,
        style = MaterialTheme.typography.titleLarge.copy(
          fontFamily = AppFontFamilyEmphasized
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = history.artist,
        style = MaterialTheme.typography.bodyMedium
      )
      Spacer(modifier = Modifier.height(8.dp))
      val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
      Text(
        text = history.timestamp.format(timeFormatter),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.outline
      )
    }
  }
}
