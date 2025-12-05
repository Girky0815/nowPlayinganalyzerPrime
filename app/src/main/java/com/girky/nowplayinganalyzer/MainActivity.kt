package com.girky.nowplayinganalyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.girky.nowplayinganalyzer.data.AppTheme
import com.girky.nowplayinganalyzer.ui.components.NavItem
import com.girky.nowplayinganalyzer.ui.components.ResponsiveNavBar
import com.girky.nowplayinganalyzer.ui.history.HistoryScreen
import com.girky.nowplayinganalyzer.ui.settings.SettingsScreen
import com.girky.nowplayinganalyzer.ui.settings.SettingsViewModel
import com.girky.nowplayinganalyzer.ui.theme.NowPlayinganalyzerPrimeTheme

class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by settingsViewModel.theme.collectAsState()
            
            val isDark = when (theme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            NowPlayinganalyzerPrimeTheme(darkTheme = isDark) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "history"

    // 画面サイズ取得 (簡易的)
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val isWideScreen = screenWidthDp >= 600.dp

    val navItems = listOf(
        NavItem("history", Icons.Default.DateRange, "履歴"),
        NavItem("settings", Icons.Default.Settings, "設定")
    )

    Scaffold(
        bottomBar = {
            // 浮遊させるためにBoxでラップするが、ScaffoldのbottomBarスロットを使うことで
            // innerPaddingが自動的に計算され、コンテンツが隠れるのを防ぐ
            ResponsiveNavBar(
                items = navItems,
                selectedRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                isWideScreen = isWideScreen
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "history",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("history") {
                HistoryScreen(
                    contentPadding = innerPadding
                )
            }
            composable("settings") {
                SettingsScreen(
                    contentPadding = innerPadding
                )
            }
        }
    }
}