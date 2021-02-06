package com.jankrb.fff_layout.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.jankrb.fff_layout.MainActivity
import com.jankrb.fff_layout.R

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        var root = inflater.inflate(R.layout.settings_fragment, container, false)

        var languageList = listOf("Deutsch", "English", "Polish", "Spain")
        var languageSelector: Spinner = root.findViewById(R.id.settings_language_selector)
        var languageSelectorAdapter: ArrayAdapter<String> = ArrayAdapter((context as MainActivity), R.layout.support_simple_spinner_dropdown_item, languageList)
        languageSelector.adapter = languageSelectorAdapter

        return root
    }

}