package com.jankrb.fff_app.ui.scan

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.util.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.jankrb.fff_app.MainActivity
import com.jankrb.fff_app.R
import com.jankrb.fff_app.ui.scanned.ScannedFragment


class ScanFragment : Fragment() {

    // Barcode Scanner
    private lateinit var scansViewModel: ScanViewModel
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector

    // GPS
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestCodeGPSPermission = 99

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        scansViewModel =
                ViewModelProvider(this).get(ScanViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_scan, container, false)

        // Check if app has camera access, if not request permissions, else setup camera
        if (ActivityCompat.checkSelfPermission(
                (activity as MainActivity),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
            askForCameraPermission()
        } else {
            setupControls()
        }

        // Check if app has gps access, if not request permissions, else setup locationmanager
        if (ActivityCompat.checkSelfPermission(
                context as MainActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            askForGPSPermission() // Request permission, if has no permissions
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
        val cameraSurfaceView = root.findViewById<SurfaceView>(R.id.surfaceView)
        cameraSurfaceView.holder.addCallback(surgaceCallback) // Apply Create/Change/Delete Callback
        detector.setProcessor(processor) // Apply detector processor to detector
    }

    /**
     * Request camera permission for scanner
     */
    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            (activity as MainActivity),
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
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

        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) { // Callback is not empty
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Callback permission is granted
                setupControls()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show() // Callback permission is denied
            }
        } else if (requestCode == requestCodeGPSPermission && grantResults.isNotEmpty()) { // Callback is not empty
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED)  {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show() // Callback permission is denied
            }
        }
    }

    /**
     * Request gps permission
     */
    private fun askForGPSPermission() {
        ActivityCompat.requestPermissions(
            context as MainActivity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            requestCodeGPSPermission
        )
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

        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        (activity as MainActivity),
                        android.Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED) {
                    askForCameraPermission() // Request permission, if has no permissions
                }

                cameraSource.start(holder) // Start camera
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
                    cameraSource.stop() // Stop Camera

                    // GPS
                    if (ActivityCompat.checkSelfPermission(
                            context as MainActivity,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED) {
                        askForGPSPermission() // Request permission, if has no permissions
                    }

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location->
                            if (location != null) {
                                // Work with location from here
                            }
                        }

                    // TODO: Show Scanned Fragment
                }
            }
        }
    }
}