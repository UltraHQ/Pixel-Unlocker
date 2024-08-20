package google.hates.pixel_unlocker

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import google.hates.pixel_unlocker.Constants.PREF_DEVICE_TO_SPOOF
import google.hates.pixel_unlocker.Constants.PREF_ENABLE_VERBOSE_LOGS
import google.hates.pixel_unlocker.Constants.PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE
import google.hates.pixel_unlocker.Constants.PREF_SPOOF_ANDROID_VERSION_MANUAL


class DeviceSpoofer : IXposedHookLoadPackage {

    private fun log(message: String) {
        XposedBridge.log("PixelUnlocker: $message")
        Log.d("PixelUnlocker", message)
    }

    private val pref by lazy {
        XSharedPreferences(BuildConfig.APPLICATION_ID, Constants.SHARED_PREF_FILE_NAME)
    }

    private val verboseLog: Boolean by lazy {
        pref.getBoolean(PREF_ENABLE_VERBOSE_LOGS, false)
    }

    private val androidVersionToSpoof: DeviceProps.AndroidVersion? by lazy {
        if (pref.getBoolean(
                PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE,
                false
            )
        ) finalDeviceToSpoof?.androidVersion
        else {
            pref.getString(PREF_SPOOF_ANDROID_VERSION_MANUAL, null)?.let {
                DeviceProps.getAndroidVersionFromLabel(it)
            }
        }
    }

    private val finalDeviceToSpoof by lazy {
        val deviceName = pref.getString(PREF_DEVICE_TO_SPOOF, DeviceProps.defaultDeviceName)
        log("Device spoof: $deviceName")
        DeviceProps.getDeviceProps(deviceName)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {

        log("Loaded DeviceSpoofer for ${lpparam?.packageName}")
        log("Device spoof: ${finalDeviceToSpoof?.deviceName}")

        finalDeviceToSpoof?.props?.run {
            if (keys.isEmpty()) return
            val classLoader = lpparam?.classLoader ?: return

            val classBuild = XposedHelpers.findClass("android.os.Build", classLoader)
            keys.forEach {
                XposedHelpers.setStaticObjectField(classBuild, it, this[it])
                if (verboseLog) log("DEVICE PROPS: $it - ${this[it]}")
            }
        }

        androidVersionToSpoof?.getAsMap()?.run {
            val classLoader = lpparam?.classLoader ?: return
            val classBuild = XposedHelpers.findClass("android.os.Build.VERSION", classLoader)

            keys.forEach {
                XposedHelpers.setStaticObjectField(classBuild, it, this[it])
                if (verboseLog) log("VERSION SPOOF: $it - ${this[it]}")
            }
        }
    }
}