package com.jankrb.fff_layout.home

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.jankrb.fff_layout.R

class HomeNewsListAdapter(private val context: Activity, private val title: Array<String>, private val description: Array<String>, private val createdAt: Array<String>)
    : ArrayAdapter<String>(context, R.layout.home_news_element, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.home_news_element, null, true)

        // Get view elements for list view
        val titleText = rowView.findViewById(R.id.title) as TextView
        val descriptionText = rowView.findViewById(R.id.description) as TextView

        titleText.text = title[position] + " - " + createdAt[position] // Apply text to title
        descriptionText.text = description[position] // Apply text to description

        return rowView
    }

}