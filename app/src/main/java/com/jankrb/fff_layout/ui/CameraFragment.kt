package com.jankrb.fff_layout.ui

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jankrb.fff_layout.MainActivity
import com.jankrb.fff_layout.R


class CameraFragment : Fragment(), QRCodeReaderView.OnQRCodeReadListener {

    companion object {
        fun newInstance() = CameraFragment()
    }

    private val requestCodePermission = 1001
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private lateinit var qrCodeReaderView: QRCodeReaderView
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
            setupControls()
        } else {
            askForPermission()
        }

        // Create GPS Location Manager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context as MainActivity)

        return root
    }

    /**
     * Setup camera and detector, apply to SurfaceView
     */
    private fun setupControls() {
        qrCodeReaderView = root.findViewById(R.id.cameraSurfaceView)
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    /**
     * Request permissions
     */
    private fun askForPermission() {
        if (!hasPermissions(context, permissions)) {
            ActivityCompat.requestPermissions(
                context as MainActivity,
                permissions,
                requestCodePermission
            );
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
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show() // Callback permission is denied
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
            ) != PackageManager.PERMISSION_GRANTED) {
            askForPermission() // Request permission, if has no permissions
        }

        var locationGPS: Location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    locationGPS = location
                }
            }

        showFragment(ScannedFragment())
    }

    override fun onResume() {
        super.onResume()
        qrCodeReaderView.startCamera()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        qrCodeReaderView.stopCamera()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}