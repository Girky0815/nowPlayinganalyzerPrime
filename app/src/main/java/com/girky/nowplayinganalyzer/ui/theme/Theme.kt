package com.girky.nowplayinganalyzer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
  // Dynamic Color が有効かつ Android 12+ の場合、システムのプライマリカラーをシードにする
  val context = LocalContext.current
  val systemColorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
  } else {
    null
  }

  // システムの動的カラーからシード色を取得。取得できない場合はデフォルトの青 (Pixel Default)
  val seedColor = systemColorScheme?.primary ?: Color(0xFF4285F4)

  val dynamicColorScheme = rememberDynamicColorScheme(
    seedColor = seedColor,
    isDark = darkTheme,
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