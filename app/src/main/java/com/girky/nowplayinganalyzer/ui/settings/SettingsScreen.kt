package com.girky.nowplayinganalyzer.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.girky.nowplayinganalyzer.BuildConfig
import com.girky.nowplayinganalyzer.data.AppTheme
import com.girky.nowplayinganalyzer.ui.components.GroupedListItem

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.theme.collectAsState()
    val themes = listOf(
        Triple(AppTheme.SYSTEM, "システム設定に従う", 0),
        Triple(AppTheme.LIGHT, "ライトモード", 1),
        Triple(AppTheme.DARK, "ダークモード", 2)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            item {
                SettingsSectionHeader(text = "外観")
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(themes.size) { index ->
                val (theme, label, _) = themes[index]
                GroupedListItem(
                    index = index,
                    totalCount = themes.size,
                    onClick = { viewModel.setTheme(theme) }
                ) {
                    ThemeOptionContent(
                        text = label,
                        selected = currentTheme == theme
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SettingsSectionHeader(text = "アプリについて")
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                GroupedListItem(
                    index = 0,
                    totalCount = 1,
                    onClick = {}
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "バージョン: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}

@Composable
fun ThemeOptionContent(
    text: String,
    selected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
