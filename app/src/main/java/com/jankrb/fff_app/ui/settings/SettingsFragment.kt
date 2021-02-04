package com.jankrb.fff_app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jankrb.fff_app.MainActivity
import com.jankrb.fff_app.R
import com.jankrb.fff_app.utils.FontCache

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
                ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        var settingsTitle: TextView = root.findViewById(R.id.settings_title)
        settingsTitle?.typeface = FontCache.getTypeface("Montserrat-Medium.ttf", (context as MainActivity))

        return root
    }
}