package com.girky.nowplayinganalyzer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResponsiveNavBar(
    items: List<NavItem>,
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    // ツールバー自体には固定幅制約を課さず、内部コンテンツで幅を決定させる
    HorizontalFloatingToolbar(
        modifier = modifier,
        expanded = true,
        floatingActionButton = { /* No FAB */ }
    ) {
        // 内部コンテンツを固定幅のRowでラップして、ツールバーのサイズを安定させる
        Row(
            modifier = Modifier.width(320.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = selectedRoute == item.route
                val showLabel = isWideScreen || isSelected

                ToggleButton(
                    checked = isSelected,
                    onCheckedChange = { onNavigate(item.route) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .clip(CircleShape)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        AnimatedVisibility(
                            visible = showLabel,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Row {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
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
