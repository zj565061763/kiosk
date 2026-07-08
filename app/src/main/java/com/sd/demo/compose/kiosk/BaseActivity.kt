package com.sd.demo.compose.kiosk

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sd.demo.compose.kiosk.theme.AppTheme

open class BaseActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    super.onCreate(savedInstanceState)
    hideSystemUI()
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    if (ev?.action == MotionEvent.ACTION_DOWN) {
      hideSystemUI()
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
      content()
    }
  }

  private fun hideSystemUI() {
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
    controller.hide(WindowInsetsCompat.Type.systemBars())
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT
  }
}