package com.sd.demo.compose.kiosk

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sd.demo.compose.kiosk.theme.AppTheme

class NewActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setPageContent {
      AppTheme {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
        ) {

        }
      }
    }
  }
}