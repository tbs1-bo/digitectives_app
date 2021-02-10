package com.jankrb.fff_layout.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.util.isNotEmpty
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.jankrb.fff_layout.MainActivity
import com.jankrb.fff_layout.R


class CameraFragment : Fragment() {

    companion object {
        fun newInstance() = CameraFragment()
    }

    private val requestCodePermission = 1001
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector
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
        detector = BarcodeDetector.Builder((activity as MainActivity)).build() // Build Barcode Detector
        cameraSource = CameraSource.Builder((activity as MainActivity), detector) // Build camera source
                .setAutoFocusEnabled(true)
                .build()

        // Apply camera source to surface
        val cameraSurfaceView = root.findViewById<SurfaceView>(R.id.cameraSurfaceView)
        cameraSurfaceView.holder.addCallback(surgaceCallback) // Apply Create/Change/Delete Callback
        detector.setProcessor(processor) // Apply detector processor to detector
    }

    /**
     * Request permissions
     */
    private fun askForPermission() {
        if (!hasPermissions(context, permissions)) {
            ActivityCompat.requestPermissions(context as MainActivity, permissions, requestCodePermission);
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

    /**
     * Surface Callback for Changing, Deleting and Creating the Surface
     */
    private val surgaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            cameraSource.stop()
        }

        @SuppressLint("MissingPermission")
        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                if (!hasPermissions(context, permissions)) {
                    askForPermission()
                } else {
                    cameraSource.start(holder) // Start camera
                }
            } catch (exception: Exception) {
                Toast.makeText(context, "An error occurred.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Barcode Detector Processor
     */
    private val processor = object : Detector.Processor<Barcode> {
        override fun release() {} // Processor released from detector

        /**
         * Func to receive processed camera data (e.g. Barcode details or null)
         */
        override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
            if (detections != null && detections.detectedItems.isNotEmpty()) { // Result is not null
                val qrCodes : SparseArray<Barcode> = detections.detectedItems // Get all detected codes
                val code = qrCodes.valueAt(0) // Get single/first detected code

                if (code != null) { // Check if code is not null
                    Log.i("SCAN", code.displayValue) // Debug message for Logcat

                    // GPS
                    if (ActivityCompat.checkSelfPermission(
                            context as MainActivity,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED) {
                        askForPermission() // Ask for permission if no perms
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
            }
        }
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