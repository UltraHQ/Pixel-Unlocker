package google.hates.pixel_unlocker

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import google.hates.pixel_unlocker.Constants.PREF_ENABLE_VERBOSE_LOGS
import google.hates.pixel_unlocker.Constants.PREF_OVERRIDE_ROM_FEATURE_LEVELS
import google.hates.pixel_unlocker.Constants.PREF_SPOOF_FEATURES_LIST
import google.hates.pixel_unlocker.Constants.SHARED_PREF_FILE_NAME

class FeatureSpoofer : IXposedHookLoadPackage {

    private val CLASS_APPLICATION_MANAGER = "android.app.ApplicationPackageManager"
    private val METHOD_HAS_SYSTEM_FEATURE = "hasSystemFeature"

    private fun log(message: String) {
        XposedBridge.log("PixelUnlocker: $message")
        Log.d("PixelUnlocker", message)
    }

    private val pref by lazy {
        XSharedPreferences(BuildConfig.APPLICATION_ID, SHARED_PREF_FILE_NAME).apply {
            log("Preference location: ${file.canonicalPath}")
        }
    }

    private val verboseLog: Boolean by lazy {
        pref.getBoolean(PREF_ENABLE_VERBOSE_LOGS, false)
    }

    private val finalFeaturesToSpoof: List<String> by lazy {

        val defaultFeatures = DeviceProps.defaultFeatures
        val defaultFeatureLevelsName = defaultFeatures.map { it.displayName }.toSet()

        val featureFlags =
            pref.getStringSet(PREF_SPOOF_FEATURES_LIST, defaultFeatureLevelsName)?.let { set ->
                val eligibleFeatures: List<DeviceProps.Features> =
                    when {
                        set.isEmpty() -> {
                            log("Feature flags init: EMPTY SET")
                            listOf()
                        }

                        set == defaultFeatureLevelsName -> {
                            log("Feature flags init: DEFAULT SET")
                            defaultFeatures
                        }

                        else -> DeviceProps.allFeatures.filter { set.contains(it.displayName) }
                    }
                val allFeatureFlags = ArrayList<String>(0)

                eligibleFeatures.forEach {
                    allFeatureFlags.addAll(it.featureFlags)
                }
                allFeatureFlags
            } ?: listOf()

        featureFlags.apply {
            log("Pass TRUE for feature flags: $featureFlags")
        }
    }

    private val overrideCustomROMLevels by lazy {
        pref.getBoolean(PREF_OVERRIDE_ROM_FEATURE_LEVELS, true)
    }

    private val featuresNotToSpoof: List<String> by lazy {
        val allFeatureFlags = ArrayList<String>(0)

        DeviceProps.allFeatures.map { it.featureFlags }.forEach {
            allFeatureFlags.addAll(it)
        }

        allFeatureFlags.filter { it !in finalFeaturesToSpoof }.apply {
            log("Pass FALSE for feature flags: $this")
        }
    }

    private fun spoofFeatureEnquiryResultIfNeeded(param: XC_MethodHook.MethodHookParam?) {
        val arguments = param?.args?.toList()
        var passFeatureTrue = false
        var passFeatureFalse = false

        arguments?.forEach {
            if (it.toString() in finalFeaturesToSpoof) passFeatureTrue = true
            else if (overrideCustomROMLevels) {
                if (it.toString() in featuresNotToSpoof) passFeatureFalse = true
            }
        }

        if (passFeatureTrue) param?.setResult(true).apply {
            if (verboseLog) log("TRUE - feature args: $arguments")
        }
        else if (passFeatureFalse) param?.setResult(false).apply {
            if (verboseLog) log("FALSE - feature args: $arguments")
        }
        else {
            if (verboseLog) log("NO_CHANGE - feature args: $arguments")
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {

        log("Loaded FeatureSpoofer for ${lpparam?.packageName}")

        XposedHelpers.findAndHookMethod(CLASS_APPLICATION_MANAGER,
            lpparam?.classLoader,
            METHOD_HAS_SYSTEM_FEATURE,
            String::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    spoofFeatureEnquiryResultIfNeeded(param)
                }
            })

        XposedHelpers.findAndHookMethod(CLASS_APPLICATION_MANAGER,
            lpparam?.classLoader,
            METHOD_HAS_SYSTEM_FEATURE,
            String::class.java,
            Int::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    spoofFeatureEnquiryResultIfNeeded(param)
                }
            })
    }
}