package com.sd.lib.kiosk

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("StaticFieldLeak")
object KioskHandler {
  private lateinit var _context: Context

  private val _isDeviceOwnerFlow = MutableStateFlow(false)
  private val _isEnabledFlow = MutableStateFlow(false)

  val isDeviceOwnerFlow: StateFlow<Boolean> = _isDeviceOwnerFlow.asStateFlow()
  val isEnabledFlow: StateFlow<Boolean> = _isEnabledFlow.asStateFlow()

  private val _manager by lazy {
    checkInit()
    _context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
  }

  val packageInstaller by lazy {
    checkInit()
    KioskPackageInstaller(_context)
  }

  fun init(context: Context) {
    if (!::_context.isInitialized) {
      _context = context.applicationContext
      Looper.getMainLooper().queue.addIdleHandler {
        updateFlow()
        true
      }
    }
    updateFlow()
  }

  fun isDeviceOwner(): Boolean {
    return _manager.isDeviceOwnerApp(_context.packageName).also { isDeviceOwner ->
      _isDeviceOwnerFlow.value = isDeviceOwner
    }
  }

  fun isEnabled(): Boolean {
    val manager = _context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return (manager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE).also { isEnabled ->
      _isEnabledFlow.value = isEnabled
    }
  }

  fun setEnabled(enabled: Boolean, activity: Activity) {
    if (!isDeviceOwner()) return
    val admin = ComponentName(_context, KioskDeviceAdminReceiver::class.java)
    if (enabled) {
      _manager.setLockTaskPackages(admin, arrayOf(_context.packageName))
      _manager.setStatusBarDisabled(admin, true)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        _manager.setLockTaskFeatures(admin, DevicePolicyManager.LOCK_TASK_FEATURE_NONE)
      }
      runCatching { activity.startLockTask() }.also {
        activity.postDelayed { updateFlow() }
      }
    } else {
      runCatching { activity.stopLockTask() }.also {
        _manager.setStatusBarDisabled(admin, false)
        activity.postDelayed {
          if (isDeviceOwner() && !isEnabled()) {
            _manager.setLockTaskPackages(admin, emptyArray())
          }
          updateFlow()
        }
      }
    }
  }

  private fun updateFlow() {
    isDeviceOwner()
    isEnabled()
  }

  private fun Activity.postDelayed(
    delayMillis: Long = 100,
    block: () -> Unit,
  ) {
    window.decorView.postDelayed({ block() }, delayMillis)
  }

  private fun checkInit() {
    if (!::_context.isInitialized) error("KioskHandler has not been initialized. Call init(context) first.")
  }
}
