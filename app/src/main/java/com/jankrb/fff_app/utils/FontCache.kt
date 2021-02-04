package com.jankrb.fff_app.utils

import android.content.Context
import android.graphics.Typeface
import java.lang.Exception
import java.util.*

object FontCache {

    // Cached Fonts
    private val fontCache = Hashtable<String, Typeface>()

    /**
     * Get Typeface for text. Fonts will be cached to prevent memory leaks.
     * @param name String: Name of the font inside assets/font
     * @param context Context: Activity context for loading assets
     * @return Typeface: Typeface used for text
     */
    fun getTypeface(name: String, context:Context): Typeface? {
        var tf = fontCache.get(name)

        if (tf == null) {
            try {
                tf = Typeface.createFromAsset(context.assets, "font/$name")
            } catch (e: Exception) {
                return null;
            }

            fontCache.put(name, tf)
        }
        return tf
    }

}