package com.sd.lib.kiosk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller

internal class PackageReplacedReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val action = intent.action
    val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)

    if (action == KioskPackageInstaller.ACTION_INSTALL_COMPLETE) {
      KioskHandler.packageInstaller.setInstalling(false)
    }

    if (action == Intent.ACTION_MY_PACKAGE_REPLACED ||
      (action == KioskPackageInstaller.ACTION_INSTALL_COMPLETE && status == PackageInstaller.STATUS_SUCCESS)
    ) {
      context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        context.startActivity(this)
      }
    }
  }
}
