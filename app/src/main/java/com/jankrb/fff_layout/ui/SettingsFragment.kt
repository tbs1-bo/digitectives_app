package com.jankrb.fff_layout.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jankrb.fff_layout.Language
import com.jankrb.fff_layout.LanguageConfiguration
import com.jankrb.fff_layout.MainActivity
import com.jankrb.fff_layout.R

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        var root = inflater.inflate(R.layout.settings_fragment, container, false)

        // Ignore call when creating the list
        var createSelection = true

        var languageList = ArrayList<Language>()// Create list of available languages
        languageList.add(Language("English", "en"))
        languageList.add(Language("Deutsch", "de"))

        val selectedLanguageID = MainActivity.preferences.getString("selected_language", "en")!!

        val selectorList = getStringListByLanguages(languageList)
        val languageSelector: Spinner = root.findViewById(R.id.settings_language_selector) // Get dropdown menu
        val languageSelectorAdapter: ArrayAdapter<String> = ArrayAdapter((context as MainActivity), R.layout.support_simple_spinner_dropdown_item, selectorList) // Create a adapter for dropdown elements
        languageSelector.adapter = languageSelectorAdapter // Map adapter onto dropdown
        languageSelector.setSelection(getIndexFromLanguageListById(languageList, selectedLanguageID))
        languageSelector.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(view == null) {
                    return;
                }

                if(createSelection) {
                    createSelection = false
                    return
                }

                // Update selected language flag
                val language = getLanguageByName(languageList, languageSelector.selectedItem.toString())!!
                MainActivity.preferences.edit().putString("selected_language", language.languageID).apply()
                showUpToast(language)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return root
    }

    private fun showUpToast(language: Language) {
        val text = resources.getString(R.string.settings_language_changed_message).replace("%d", "'${language.name}' (${language.languageID})")
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText((context as MainActivity), text, duration)
        toast.show()
    }

    /**
     * @return string list of all language names
     */
    private fun getStringListByLanguages(languageList: ArrayList<Language>) : ArrayList<String> {
        val list = ArrayList<String>()

        for(language in languageList) {
            list.add(language.name)
        }

        return list
    }

    /**
     * @return language by name
     */
    private fun getLanguageByName(languageList: ArrayList<Language>, name: String) : Language? {
        for(language in languageList) {
            if(language.name.equals(name, ignoreCase = true)) {
                return language
            }
        }
        return null
    }

    /**
     * @return get index of language from a language list
     */
    private fun getIndexFromLanguageListById(languageList: ArrayList<Language>, id: String): Int {
        for (i in 0 until languageList.size) {
            val language = languageList.get(i)
            if(language.languageID == id)
                return i
        }
        return 0
    }
}