package google.hates.pixel_unlocker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import google.hates.pixel_unlocker.Constants.PREF_DEVICE_TO_SPOOF
import google.hates.pixel_unlocker.Constants.PREF_OVERRIDE_ROM_FEATURE_LEVELS
import google.hates.pixel_unlocker.Constants.PREF_SPOOF_FEATURES_LIST
import google.hates.pixel_unlocker.Constants.SHARED_PREF_FILE_NAME

class FragmentMain : Fragment() {

    private lateinit var pref: SharedPreferences

    private fun showRebootSnack() {
        val rootView = requireView().findViewById<ScrollView>(R.id.root_view_for_snackbar)
        Snackbar.make(rootView, R.string.please_force_stop_google_photos, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun peekFeatureFlagsChanged(textView: TextView) {
        textView.run {
            alpha = 1.0f
            animate().alpha(0.0f).apply {
                duration = 1000
                startDelay = 3000
            }.start()
        }
    }

    private val childActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            showRebootSnack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pref = requireActivity().getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)
            .also {
                if (it == null) {
                    AlertDialog.Builder(requireContext()).setMessage(R.string.module_not_enabled)
                        .setPositiveButton(R.string.quit) { _, _ -> requireActivity().finish() }
                        .setNegativeButton(R.string.ignore, null).setCancelable(false).show()
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val featureFlagsChanged = view.findViewById<TextView>(R.id.feature_flags_changed)
        val overrideROMFeatureLevels = view.findViewById<SwitchCompat>(R.id.override_rom_feature_levels)
        val deviceSpooferSpinner = view.findViewById<Spinner>(R.id.device_spoofer_spinner)
        val advancedOptions = view.findViewById<TextView>(R.id.advanced_options)
        val perAppSpoof = view.findViewById<LinearLayout>(R.id.per_app_spoof)
        val perAppSpoofText = view.findViewById<TextView>(R.id.per_app_spoof_text)

        fun updatePerAppSpoofText(selectedDeviceName: String) {
            perAppSpoofText.text = getString(if (selectedDeviceName == "None") R.string.only_spoof_these_apps else R.string.per_app_spoof)
        }

        val selectedDeviceName = pref.getString(PREF_DEVICE_TO_SPOOF, DeviceProps.defaultDeviceName)
        selectedDeviceName?.let { updatePerAppSpoofText(it) }

        setHasOptionsMenu(true)

        overrideROMFeatureLevels.apply {
            isChecked = pref.getBoolean(PREF_OVERRIDE_ROM_FEATURE_LEVELS, true)
            setOnCheckedChangeListener { _, isChecked ->
                pref.edit().putBoolean(PREF_OVERRIDE_ROM_FEATURE_LEVELS, isChecked).apply()
                showRebootSnack()
            }
        }

        perAppSpoof.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AppSpecificSpoofingFragment())
                .addToBackStack(null).commit()
        }

        deviceSpooferSpinner.apply {
            val deviceNames = DeviceProps.allDevices.map { it.deviceName }
            val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, deviceNames)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = aa
            val defaultSelection = pref.getString(PREF_DEVICE_TO_SPOOF, DeviceProps.defaultDeviceName)
            setSelection(aa.getPosition(defaultSelection), false)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val deviceName = aa.getItem(position) ?: return
                    updatePerAppSpoofText(deviceName)

                    pref.edit().apply {
                        putString(PREF_DEVICE_TO_SPOOF, deviceName)
                        putStringSet(PREF_SPOOF_FEATURES_LIST, DeviceProps.getFeaturesUpToFromDeviceName(deviceName))
                        apply()
                    }

                    peekFeatureFlagsChanged(featureFlagsChanged)
                    showRebootSnack()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        advancedOptions.apply {
            paintFlags = Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                childActivityLauncher.launch(Intent(requireContext(), AdvancedOptionsActivity::class.java))
            }
        }
    }
}