package com.sd.demo.compose.kiosk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sd.demo.compose.kiosk.theme.AppTheme
import com.sd.lib.kiosk.KioskHandler

open class BaseActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    super.onCreate(savedInstanceState)
  }

  /** 设置页面内容 */
  protected fun setPageContent(content: @Composable () -> Unit) {
    setContent { PageContent(content) }
  }

  /** 页面内容 */
  @Composable
  protected fun PageContent(content: @Composable () -> Unit) {
    AppTheme {
      val isKioskEnabled by KioskHandler.isEnabledFlow.collectAsStateWithLifecycle()
      LaunchedEffect(isKioskEnabled) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (isKioskEnabled) {
          controller.hide(WindowInsetsCompat.Type.systemBars())
          controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
          controller.show(WindowInsetsCompat.Type.systemBars())
        }
      }
      content()
    }
  }
}