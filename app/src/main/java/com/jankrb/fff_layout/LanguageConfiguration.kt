package com.jankrb.fff_layout

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*

class LanguageConfiguration {

    fun setLanguage(context: Context, languageID: String) {
        // updating the language for devices above android nougat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, languageID);
        }
        // for devices having lower version of android os
        updateResourcesLegacy(context, languageID);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String) : Context {
        val locale = Locale(language);
        Locale.setDefault(locale);

        val configuration = context.resources.configuration;
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String) : Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources: Resources = context.resources

        val configuration: Configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
}