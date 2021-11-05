package com.jankrb.fff_layout.ui

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jankrb.fff_layout.MainActivity
import com.jankrb.fff_layout.R

class ScannedFragment : Fragment() {

    companion object {
        fun newInstance() = ScannedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var root = inflater.inflate(R.layout.scanned_fragment, container, false)

        // Show scanner after delay of 5s
        Handler().postDelayed({ kotlin.run {
            showFragment(CameraFragment())
        } }, 5000)

        return root
    }

    private fun showFragment(fragment: Fragment) {
        try {
            (context as MainActivity).supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, fragment) // Replace current fragment
                commit() // Apply changes
            }
        } catch (ignored: Exception) {} // Probably crashing when changing fragment
    }
}