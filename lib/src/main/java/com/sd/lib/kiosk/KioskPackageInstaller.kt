package com.sd.lib.kiosk

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileInputStream

class KioskPackageInstaller internal constructor(private val context: Context) {
  private val _isInstallingFlow = MutableStateFlow(false)
  val isInstallingFlow: StateFlow<Boolean> = _isInstallingFlow.asStateFlow()

  fun installPackage(apkFile: File): Boolean {
    if (!KioskHandler.isDeviceOwner()) return false
    if (!apkFile.exists()) return false

    val installer = context.packageManager.packageInstaller
    val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)

    return try {
      _isInstallingFlow.value = true
      val sessionId = installer.createSession(params)
      val session = installer.openSession(sessionId)

      val out = session.openWrite("KioskUpdate", 0, apkFile.length())
      val input = FileInputStream(apkFile)
      input.use { it.copyTo(out) }
      session.fsync(out)
      out.close()

      val intent = Intent(ACTION_INSTALL_COMPLETE)
      intent.setPackage(context.packageName)

      val pendingIntent = PendingIntent.getBroadcast(
        context,
        sessionId,
        intent,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
          PendingIntent.FLAG_UPDATE_CURRENT
        }
      )

      session.commit(pendingIntent.intentSender)
      session.close()
      true
    } catch (e: Exception) {
      e.printStackTrace()
      _isInstallingFlow.value = false
      false
    }
  }

  internal fun setInstalling(installing: Boolean) {
    _isInstallingFlow.value = installing
  }

  companion object {
    internal const val ACTION_INSTALL_COMPLETE = "com.sd.lib.kiosk.ACTION_INSTALL_COMPLETE"
  }
}
