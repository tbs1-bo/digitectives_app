package com.jankrb.fff_layout.ui

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jankrb.fff_layout.MainActivity
import com.jankrb.fff_layout.R
import com.jankrb.fff_layout.dbclasses.dbqueries
import java.text.SimpleDateFormat
import java.util.*


class CameraFragment : Fragment(), QRCodeReaderView.OnQRCodeReadListener {

    companion object {
        fun newInstance() = CameraFragment()
    }

    private val requestCodePermission = 1001
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var qrCodeReaderView: QRCodeReaderView? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.camera_fragment, container, false)

        // Check if app has gps access, if not request permissions, else setup locationmanager
        if (
            (ActivityCompat.checkSelfPermission(
                context as MainActivity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) &&
            (ActivityCompat.checkSelfPermission(
                context as MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            // Start camera and gps, if permissions granted
            setupControls()
        } else {
            askForPermission() // Ask for permissions
            // TODO: Possible error on oppo phones. Oppo phones seems to use another permission api
        }

        // Create GPS Location Manager
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context as MainActivity)

        return root
    }

    /**
     * Setup camera and detector, apply to SurfaceView
     */
    private fun setupControls() {
        qrCodeReaderView = root.findViewById(R.id.cameraSurfaceView)
        qrCodeReaderView!!.setOnQRCodeReadListener(this)

        // Use this function to enable/disable decoding
        qrCodeReaderView!!.setQRDecodingEnabled(true)

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView!!.setAutofocusInterval(2000L)

        // Use this function to enable/disable Torch
        qrCodeReaderView!!.setTorchEnabled(true)

        // Use this function to set front camera preview
        qrCodeReaderView!!.setFrontCamera()

        // Use this function to set back camera preview
        qrCodeReaderView!!.setBackCamera()
    }

    /**
     * Request permissions
     */
    private fun askForPermission() {
        if (!hasPermissions(context, permissions)) {
            requestPermissions(permissions, requestCodePermission)
            //ActivityCompat.requestPermissions(
            //  context as MainActivity,
            //  permissions,
            //  requestCodePermission
            //)
        }
    }

    /**
     * Check if App has permissions
     */
    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Callback function for func askForCameraPermission & askForGPSPermission
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestCodePermission && grantResults.isNotEmpty()) { // Callback is not empty
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT)
                        .show() // Callback permission is denied
                    return
                }
            }

            showFragment(CameraFragment())
        }
    }

    private fun showFragment(fragment: Fragment) {
        (context as MainActivity).supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }

    override fun onQRCodeRead(text: String?, points: Array<PointF?>?) {
        Log.i("SCANNED", "Found: $text")

        // GPS
        if (ActivityCompat.checkSelfPermission(
                context as MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForPermission() // Request permission, if has no permissions
        }

        if (text != null) {
            //Split QR Code Text in insect name & insect id
            val splittedText = text.split("#")
            if (splittedText.size == 2) {
                var locationGPS: Location

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            locationGPS = location
                            val latitude = locationGPS.latitude.toString()
                            val longitude = locationGPS.longitude.toString()
                            val altitude = locationGPS.altitude.toString()
                            //
                            //only for debug, prints to console
                            Log.i(
                                "SCANNED",
                                "Breitengrad: $latitude / Breitengrad: $longitude / Altitude: $altitude"
                            )

                            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val timestamp: String = simpleDateFormat.format(Date())

                            dbqueries.addToLocalDatabase(
                                splittedText[0],
                                splittedText[1],
                                latitude,
                                longitude,
                                altitude,
                                timestamp,
                                0
                            )
                        }
                    }
                showFragment(ScannedFragment())
            }
        }
    }


    override fun onResume() {
        super.onResume()
        // Camera can only be started when qrCodeReaderView has been initialized
        if(qrCodeReaderView != null) {
            qrCodeReaderView!!.startCamera()
        }
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        if(qrCodeReaderView != null) {
            qrCodeReaderView!!.stopCamera()
        }
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}