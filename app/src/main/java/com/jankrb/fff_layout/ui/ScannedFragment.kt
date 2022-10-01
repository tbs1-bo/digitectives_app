package com.jankrb.fff_layout.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jankrb.fff_layout.MainActivity
import com.jankrb.fff_layout.R

class ScannedFragment : Fragment() {

    companion object {
        fun newInstance() = ScannedFragment()
    }

    private lateinit var scannedSubtitleSeconds: TextView
    private lateinit var countDownTimer: CountDownTimer

    // Cool down in seconds for the next scan
    private val scanCoolDownTime: Int = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.scanned_fragment, container, false)

        scannedSubtitleSeconds = root.findViewById(R.id.scanned_subtitle)

        // Create countDownTimer and start
        countDownTimer = createTimer()
        countDownTimer.start()

        return root
    }

    /**
     * @return a countDownTimer object which counts down and switches back to the CameraFragment when finished
     */
    private fun createTimer() : CountDownTimer {
        var pastSeconds = scanCoolDownTime
        return object: CountDownTimer((scanCoolDownTime * 1000).toLong(), 1000) {
            // Is being called every 1000ms (=1Sec.)
            override fun onTick(millisUntilFinished: Long) {
                // Update text view
                updateSeconds(pastSeconds, scannedSubtitleSeconds)
                // Count past seconds
                pastSeconds--
            }

            // Is being called when finished
            override fun onFinish() {
                showFragment(CameraFragment())
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        try {
            (context as MainActivity).supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, fragment) // Replace current fragment
                commit() // Apply changes
            }
        } catch (ignored: Exception) {} // Probably crashing when changing fragment
    }

    private fun updateSeconds(seconds: Int, textView: TextView) {
        textView.text = resources.getString(R.string.scanned_subtitle, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Cancel all timer actions when leaving fragment
        this.countDownTimer.cancel()
    }
}