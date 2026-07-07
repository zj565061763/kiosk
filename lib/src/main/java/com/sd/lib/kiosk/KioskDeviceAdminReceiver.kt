package com.sd.lib.kiosk

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

internal class KioskDeviceAdminReceiver : DeviceAdminReceiver() {
  override fun onEnabled(context: Context, intent: Intent) {
    super.onEnabled(context, intent)
    KioskHandler.init(context)
  }

  override fun onDisabled(context: Context, intent: Intent) {
    super.onDisabled(context, intent)
    KioskHandler.init(context)
  }
}