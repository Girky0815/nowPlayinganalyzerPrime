package com.girky.nowplayinganalyzer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.dynamiccolor.ColorSpec // 追加: これがないと SPEC_2025 が見つかりません

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NowPlayinganalyzerPrimeTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit
) {
  val seedColor = Color(0xFF4285F4)

  val dynamicColorScheme = rememberDynamicColorScheme(
    seedColor = seedColor,
    isDark = darkTheme, // 修正: パラメータ名は 'darkTheme' ではなく 'isDark'
    style = PaletteStyle.TonalSpot, // 修正: ここにカンマ (,) を追加
    specVersion = ColorSpec.SpecVersion.SPEC_2025
  )

  MaterialExpressiveTheme(
    colorScheme = dynamicColorScheme,
    typography = Typography,
    motionScheme = MotionScheme.expressive(),
    content = content
  )
}