package com.girky.nowplayinganalyzer.ui.settings

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
    contentPadding: PaddingValues = PaddingValues(0.dp), // Padding受け取り
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.theme.collectAsState()
    val layoutDirection = androidx.compose.ui.platform.LocalLayoutDirection.current

    // Scaffoldの背景色ではなく、コンテンツ全体を包むSurfaceかLazyColumnの背景で色を管理するが、
    // 履歴画面と同じようにLazyColumnのリストアイテムとして表示するため、
    // ここではScaffoldを使わず、LazyColumnにPaddingを適用する。
    // ただし、Edge-to-Edgeでシステムバーの背景を制御するため、全体のコンテナが必要かもしれない。
    // 親のScaffold (MainActivity) の背景色がSurfaceであれば問題ないが、
    // ここだけSurfaceContainerHighにしたい場合は、Box等で全体を覆う必要がある。
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = 16.dp + contentPadding.calculateTopPadding(),
                bottom = 16.dp + contentPadding.calculateBottomPadding(),
                start = 16.dp + contentPadding.calculateLeftPadding(layoutDirection),
                end = 16.dp + contentPadding.calculateRightPadding(layoutDirection)
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp) // セクション間のスペースを広めに
        ) {
            item {
                SettingsSectionHeader(text = "外観")
                Spacer(modifier = Modifier.height(8.dp))
                
                // テーマ設定を1つのカードにまとめる
                GroupedListItem(
                    index = 0,
                    totalCount = 1,
                    onClick = {}
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        ThemeOptionRow(
                            text = "システム設定に従う", 
                            selected = currentTheme == AppTheme.SYSTEM,
                            onClick = { viewModel.setTheme(AppTheme.SYSTEM) }
                        )
                        ThemeOptionRow(
                            text = "ライトモード", 
                            selected = currentTheme == AppTheme.LIGHT,
                            onClick = { viewModel.setTheme(AppTheme.LIGHT) }
                        )
                        ThemeOptionRow(
                            text = "ダークモード", 
                            selected = currentTheme == AppTheme.DARK,
                            onClick = { viewModel.setTheme(AppTheme.DARK) }
                        )
                    }
                }
            }

            item {
                SettingsSectionHeader(text = "アプリについて")
                Spacer(modifier = Modifier.height(8.dp))

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
fun ThemeOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // Rowのクリックで制御
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
