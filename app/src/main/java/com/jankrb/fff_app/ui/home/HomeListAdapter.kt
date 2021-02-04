package com.jankrb.fff_app.ui.home

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.jankrb.fff_app.R
import com.jankrb.fff_app.utils.FontCache

class HomeListAdapter(private val context: Activity, private val title: Array<String>, private val description: Array<String>, private val createdAt: Array<String>)
    : ArrayAdapter<String>(context, R.layout.home_recent_info_element, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.home_recent_info_element, null, true)

        // Get view elements for list view
        val titleText = rowView.findViewById(R.id.title) as TextView
        val descriptionText = rowView.findViewById(R.id.description) as TextView

        titleText.text = title[position] + " - " + createdAt[position] // Apply text to title
        titleText.typeface = FontCache.getTypeface("Montserrat-Medium.ttf", context) // Apply font to title
        descriptionText.text = description[position] // Apply text to description
        descriptionText.typeface = FontCache.getTypeface("Montserrat-Light.ttf", context) // Apply font to description

        return rowView
    }

}