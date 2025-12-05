package com.girky.nowplayinganalyzer.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings

data class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun ResponsiveNavBar(
    items: List<NavItem>,
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    isWideScreen: Boolean, // パラメータは維持するが、一旦標準バーとしては無視してBottomBarにする
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            val isSelected = selectedRoute == item.route
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                }
            )
        }
    }
}

@Preview
@Composable
fun ResponsiveNavBarPreview() {
    val items = listOf(
        NavItem("history", Icons.Default.DateRange, "履歴"),
        NavItem("analysis", Icons.Default.Analytics, "分析"),
        NavItem("settings", Icons.Default.Settings, "設定")
    )
    MaterialTheme {
        ResponsiveNavBar(
            items = items,
            selectedRoute = "history",
            onNavigate = {},
            isWideScreen = false
        )
    }
}

@Preview(widthDp = 840)
@Composable
fun ResponsiveNavBarWidePreview() {
    val items = listOf(
        NavItem("history", Icons.Default.DateRange, "履歴"),
        NavItem("analysis", Icons.Default.Analytics, "分析"),
        NavItem("settings", Icons.Default.Settings, "設定")
    )
    MaterialTheme {
        ResponsiveNavBar(
            items = items,
            selectedRoute = "history",
            onNavigate = {},
            isWideScreen = true
        )
    }
}
