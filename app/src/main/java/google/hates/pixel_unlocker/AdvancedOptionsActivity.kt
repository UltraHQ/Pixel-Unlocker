package google.hates.pixel_unlocker

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import google.hates.pixel_unlocker.Constants.PREF_DEVICE_TO_SPOOF
import google.hates.pixel_unlocker.Constants.PREF_ENABLE_VERBOSE_LOGS
import google.hates.pixel_unlocker.Constants.PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE
import google.hates.pixel_unlocker.Constants.PREF_SPOOF_ANDROID_VERSION_MANUAL
import google.hates.pixel_unlocker.Constants.SHARED_PREF_FILE_NAME

class AdvancedOptionsActivity : AppCompatActivity(R.layout.advanced_options_activity) {

    private val pref by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE)
    }

    private val verboseLogging by lazy { findViewById<CheckBox>(R.id.verbose_logging) }
    private val deviceNameLabel by lazy { findViewById<TextView>(R.id.device_name_label) }
    private val androidVersionRadioGroup by lazy { findViewById<RadioGroup>(R.id.android_version_radio_group) }
    private val deviceAndroidVersion by lazy { findViewById<TextView>(R.id.device_android_version) }
    private val androidVersionSpinner by lazy { findViewById<Spinner>(R.id.android_version_spinner) }
    private val save by lazy { findViewById<Button>(R.id.save_advanced_option) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (pref == null) return

        verboseLogging.isChecked = pref.getBoolean(PREF_ENABLE_VERBOSE_LOGS, false)

        val deviceNameInPreference =
            pref.getString(PREF_DEVICE_TO_SPOOF, DeviceProps.defaultDeviceName)
        val spoofDevice = DeviceProps.getDeviceProps(deviceNameInPreference)
        deviceNameLabel.text = spoofDevice?.deviceName
        deviceAndroidVersion.text = spoofDevice?.androidVersion?.label

        val allVersionLabels = DeviceProps.allAndroidVersions.map { it.label }

        androidVersionSpinner.apply {
            val aa = ArrayAdapter(
                this@AdvancedOptionsActivity, android.R.layout.simple_spinner_item, allVersionLabels
            )

            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = aa
            val defaultSelection = pref?.getString(PREF_SPOOF_ANDROID_VERSION_MANUAL, null)
            setSelection(aa.getPosition(defaultSelection))
        }

        androidVersionRadioGroup.apply {
            // hide spinner if not to be manually set.
            setOnCheckedChangeListener { _, checkedId ->
                androidVersionSpinner.isVisible = checkedId == R.id.manually_set_android_version
            }

            val manualVersion = pref.getString(PREF_SPOOF_ANDROID_VERSION_MANUAL, null)?.trim()
            check(
                when {
                    pref.getBoolean(
                        PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE, false
                    ) -> R.id.follow_spoof_device_version

                    manualVersion != null && manualVersion in allVersionLabels -> R.id.manually_set_android_version
                    else -> R.id.dont_spoof_android_version
                }
            )
        }

        save.setOnClickListener {
            savePreferences()
        }
    }

    private fun savePreferences() {
        pref?.edit()?.run {
            putBoolean(PREF_ENABLE_VERBOSE_LOGS, verboseLogging.isChecked)

            when (androidVersionRadioGroup.checkedRadioButtonId) {
                R.id.dont_spoof_android_version -> {
                    putBoolean(PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE, false)
                    putString(PREF_SPOOF_ANDROID_VERSION_MANUAL, null)
                }

                R.id.follow_spoof_device_version -> {
                    putBoolean(PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE, true)
                    putString(PREF_SPOOF_ANDROID_VERSION_MANUAL, null)
                }

                R.id.manually_set_android_version -> {
                    putBoolean(PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE, false)
                    putString(
                        PREF_SPOOF_ANDROID_VERSION_MANUAL,
                        androidVersionSpinner.selectedItem.toString()
                    )
                }
            }
            apply()
        }
        setResult(Activity.RESULT_OK)
        finish()
    }
}