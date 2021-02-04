package com.jankrb.fff_app

import android.app.ActionBar
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jankrb.fff_app.ui.navbar.BottomNavigationViewBehavior
import com.jankrb.fff_app.utils.FontCache


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_home, R.id.navigation_info, R.id.navigation_settings, R.id.navigation_scan
                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Set Custom App Header
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.app_header)
        supportActionBar?.setDisplayShowCustomEnabled(true)

        // App Title Font
        var tv: TextView = findViewById(R.id.header_text)
        tv.typeface = FontCache.getTypeface("Montserrat-Medium.ttf", applicationContext)

        // App Title Text
        tv.text = Settings.appTitle

        // App Background Gradient
        val gd = GradientDrawable(
                GradientDrawable.Orientation.BL_TR, intArrayOf(Color.parseColor(Settings.gradientStart), Color.parseColor(Settings.gradientStop))
        )
        gd.cornerRadius = 0f // Border Radius of toolbar
        supportActionBar?.setBackgroundDrawable(gd) // Apply GradientDrawable

        supportActionBar?.show() // Replace old toolbar

    }
}