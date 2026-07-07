[![Maven Central](https://img.shields.io/maven-central/v/io.github.zj565061763.android/kiosk)](https://central.sonatype.com/search?q=g:io.github.zj565061763.android+kiosk)

# Gradle

```kotlin
implementation("io.github.zj565061763.android:kiosk:$version")
```

# Usage

```
adb shell dumpsys device_policy | grep "Device Owner"
adb shell dpm set-device-owner com.sd.demo.compose.kiosk/com.sd.lib.kiosk.KioskDeviceAdminReceiver
adb shell dpm remove-active-admin com.sd.demo.compose.kiosk/com.sd.lib.kiosk.KioskDeviceAdminReceiver
```