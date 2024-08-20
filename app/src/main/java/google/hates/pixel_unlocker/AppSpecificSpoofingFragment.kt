package google.hates.pixel_unlocker

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import google.hates.pixel_unlocker.Constants.PREF_APP_SPECIFIC_SPOOFING

class AppSpecificSpoofingFragment : Fragment() {

    private val pref by lazy {
        requireActivity().getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_app_specific_spoofing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appListContainer = view.findViewById<LinearLayout>(R.id.app_list_container)
        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterApps(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        filterApps("")
    }

    private fun filterApps(searchText: String) {
        val appListContainer = view?.findViewById<LinearLayout>(R.id.app_list_container) ?: return
        appListContainer.removeAllViews()

        val installedApps =
            requireContext().packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                .sortedBy { requireContext().packageManager.getApplicationLabel(it).toString() }
                .filter {
                    requireContext().packageManager.getApplicationLabel(it).toString()
                        .contains(searchText, ignoreCase = true)
                }

        val deviceNames = DeviceProps.allDevices.map { it.deviceName }
        val defaultDeviceName = "None"

        installedApps.forEach { app ->
            val appLabel = requireContext().packageManager.getApplicationLabel(app).toString()
            val packageName = app.packageName

            val appView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_app_spoofing, appListContainer, false)

            appView.findViewById<TextView>(R.id.app_name).text = appLabel

            val spinner = appView.findViewById<Spinner>(R.id.device_spinner)
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, deviceNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            val savedDeviceName =
                pref.getString("$PREF_APP_SPECIFIC_SPOOFING$packageName", defaultDeviceName)
            val spinnerPosition = adapter.getPosition(savedDeviceName)
            spinner.setSelection(spinnerPosition)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedDeviceName = adapter.getItem(position)
                    pref.edit()
                        .putString("$PREF_APP_SPECIFIC_SPOOFING$packageName", selectedDeviceName)
                        .apply()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            appListContainer.addView(appView)
        }
    }
}