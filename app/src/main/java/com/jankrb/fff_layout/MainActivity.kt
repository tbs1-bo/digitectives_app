package com.jankrb.fff_layout

import android.app.ActionBar
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jankrb.fff_layout.objects.PrivateSettings
import com.jankrb.fff_layout.ui.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val homeFragment = HomeFragment()
        val cameraFragment = CameraFragment()
        val infoFormatError = InfoFragment()
        val settingsFragment = SettingsFragment()

        setCurrentFragment(homeFragment)

        var bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.camera->setCurrentFragment(cameraFragment)
                R.id.info->setCurrentFragment(infoFormatError)
                R.id.settings->setCurrentFragment(settingsFragment)
            }
            true
        }

        // Set Custom App Header
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.header_toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(true)

        // App Title Text
        if (PrivateSettings.customTitle) {
            var tv: TextView = findViewById(R.id.header_text)
            tv.text = PrivateSettings.appTitle
        }

        // App Background Gradient
        val gd = GradientDrawable(
            GradientDrawable.Orientation.BL_TR, intArrayOf(Color.parseColor(PrivateSettings.gradientStart), Color.parseColor(PrivateSettings.gradientStop))
        )
        gd.cornerRadius = 0f // Border Radius of toolbar
        supportActionBar?.setBackgroundDrawable(gd) // Apply GradientDrawable

        supportActionBar?.show() // Replace old toolbar
    }

    fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
}