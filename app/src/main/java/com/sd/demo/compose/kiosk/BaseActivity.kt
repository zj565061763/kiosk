package com.sd.demo.compose.kiosk

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sd.demo.compose.kiosk.theme.AppTheme
import com.sd.lib.kiosk.KioskHandler

open class BaseActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    if (ev?.action == MotionEvent.ACTION_DOWN) {
      showSystemUI()
    }
    return super.dispatchTouchEvent(ev)
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
        showSystemUI(!isKioskEnabled)
      }
      content()
    }
  }

  private fun showSystemUI(show: Boolean = !KioskHandler.isEnabled()) {
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    if (show) {
      controller.show(WindowInsetsCompat.Type.systemBars())
    } else {
      controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
      controller.hide(WindowInsetsCompat.Type.systemBars())
      val color = androidx.compose.ui.graphics.Color.Black.copy(0.3f).toArgb()
      window.statusBarColor = color
      window.navigationBarColor = color
    }
  }
}