package com.girky.nowplayinganalyzer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * リスト内の位置に応じて角丸形状を変化させるリストアイテムコンテナ。
 * Tomato App の設定画面スタイルを模倣。
 *
 * @param index リスト内でのインデックス (0始まり)
 * @param totalCount リストの要素総数
 * @param onClick クリック時のアクション
 * @param content コンテンツ
 */
@Composable
fun GroupedListItem(
    index: Int,
    totalCount: Int,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = remember(index, totalCount) {
        getGroupedItemShape(index, totalCount)
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = shape,
        modifier = modifier
            .clip(shape) // クリック時のリップルも形状に合わせる
            .clickable(onClick = onClick)
    ) {
        content()
    }
}

fun getGroupedItemShape(index: Int, totalCount: Int): Shape {
    val outerRadius = 20.dp
    val innerRadius = 4.dp

    return when {
        // 要素が1つだけの場合: 全て 20dp
        totalCount == 1 -> RoundedCornerShape(outerRadius)

        // 一番上の要素: 上 20dp, 下 4dp
        index == 0 -> RoundedCornerShape(
            topStart = outerRadius,
            topEnd = outerRadius,
            bottomStart = innerRadius,
            bottomEnd = innerRadius
        )

        // 一番下の要素: 上 4dp, 下 20dp
        index == totalCount - 1 -> RoundedCornerShape(
            topStart = innerRadius,
            topEnd = innerRadius,
            bottomStart = outerRadius,
            bottomEnd = outerRadius
        )

        // 中間の要素: 全て 4dp
        else -> RoundedCornerShape(innerRadius)
    }
}
