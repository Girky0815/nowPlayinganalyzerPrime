package com.girky.nowplayinganalyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.girky.nowplayinganalyzer.ui.history.HistoryScreen
import com.girky.nowplayinganalyzer.ui.theme.NowPlayinganalyzerPrimeTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      NowPlayinganalyzerPrimeTheme {
        HistoryScreen()
      }
    }
  }
}