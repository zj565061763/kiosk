package com.sd.demo.compose.kiosk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sd.demo.compose.kiosk.theme.AppTheme
import com.sd.lib.kiosk.KioskHandler
import java.io.File

class MainActivity : ComponentActivity() {
  private val updateApk: File
    get() {
      val dir = getExternalFilesDir(null)!!.also { it.mkdirs() }
      return dir.resolve("update.apk")
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    setContent {
      val isDeviceOwner by KioskHandler.isDeviceOwnerFlow.collectAsStateWithLifecycle()
      val isKioskEnabled by KioskHandler.isEnabledFlow.collectAsStateWithLifecycle()
      val isInstalling by KioskHandler.packageInstaller.isInstallingFlow.collectAsStateWithLifecycle()

      if (isInstalling) {
        Dialog(
          onDismissRequest = {},
          properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(text = "Installing...", modifier = Modifier.padding(top = 8.dp))
          }
        }
      }

      LaunchedEffect(isKioskEnabled) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (isKioskEnabled) {
          controller.hide(WindowInsetsCompat.Type.systemBars())
          controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
          controller.show(WindowInsetsCompat.Type.systemBars())
        }
      }

      AppTheme {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text("isDeviceOwner:$isDeviceOwner")

          if (isDeviceOwner) {
            Button(onClick = { installUpdateApk() }) {
              Text(text = "Install ${updateApk.absolutePath}")
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
          ) {
            Text("isKioskEnabled")
            Switch(
              checked = isKioskEnabled,
              onCheckedChange = { KioskHandler.setEnabled(it, this@MainActivity) },
            )
          }
        }
      }
    }
  }

  private fun installUpdateApk() {
    KioskHandler.packageInstaller.installPackage(updateApk).also {
      logMsg { "installUpdateApk result:$it" }
    }
  }
}

inline fun logMsg(block: () -> String) {
  Log.i("sd-demo", block())
}