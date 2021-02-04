package com.jankrb.fff_app.ui.scanned

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.util.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.jankrb.fff_app.MainActivity
import com.jankrb.fff_app.R


class ScannedFragment : Fragment() {

    private lateinit var scannedViewModel: ScannedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        scannedViewModel =
                ViewModelProvider(this).get(ScannedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_scanned, container, false)



        return root
    }
}