package com.sd.demo.compose.kiosk

import android.app.Application
import com.sd.lib.kiosk.KioskHandler

class App : Application() {
  override fun onCreate() {
    super.onCreate()
    KioskHandler.init(this)
  }
}