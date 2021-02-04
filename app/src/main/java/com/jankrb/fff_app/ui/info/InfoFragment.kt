package com.jankrb.fff_app.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jankrb.fff_app.R
import com.jankrb.fff_app.utils.FontCache

class InfoFragment : Fragment() {

    private lateinit var infoViewModel: InfoViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        infoViewModel =
                ViewModelProvider(this).get(InfoViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_info, container, false)

        var listElements = arrayOf(
                root.findViewById<TextView>(R.id.about_title),
                root.findViewById<TextView>(R.id.about_text),

                root.findViewById<TextView>(R.id.howto_title),
                root.findViewById<TextView>(R.id.howto_text),

                root.findViewById<TextView>(R.id.sync_title),
                root.findViewById<TextView>(R.id.sync_text)
        )

        listElements.forEach { x ->
            run {
                x?.typeface = FontCache.getTypeface("Montserrat-Regular.ttf", root.context)
            }
        }

        return root
    }
}