package com.jankrb.fff_layout.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jankrb.fff_layout.MainActivity
import com.jankrb.fff_layout.R
import com.jankrb.fff_layout.dbclasses.Scan
import com.jankrb.fff_layout.dbclasses.ScanDao
import com.jankrb.fff_layout.dbclasses.dbvar
import com.jankrb.fff_layout.home.HomeNewsListAdapter
import com.jankrb.fff_layout.objects.PrivateSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val createdAts: MutableList<String> = mutableListOf()
    private val titles: MutableList<String> = mutableListOf()
    private val descriptions: MutableList<String> = mutableListOf()
    private lateinit var totalScannedView: TextView
    private lateinit var numberTypesView: TextView
    private lateinit var numberUnsyncedView: TextView
    private lateinit var listView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.home_fragment, container, false)

        // Home Sync Btn
        val sync_btn: Button = root.findViewById(R.id.sync_data_btn)

        //scanView = root.findViewById(R.id.home_recent_informations)
        totalScannedView = root.findViewById(R.id.stats_box_self_value)
        numberTypesView = root.findViewById(R.id.stats_box_types_value)
        numberUnsyncedView = root.findViewById(R.id.stats_box_unsynced_value)
        listView = root.findViewById<ListView>(R.id.home_recent_informations)

        val gd = GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(
                Color.parseColor(PrivateSettings.gradientStart),
                Color.parseColor(PrivateSettings.gradientStop)
            )
        )
        gd.cornerRadius = 50f
        sync_btn.background = gd

        sync_btn.setOnClickListener {
            val scanDao: ScanDao = dbvar.scanDao()
            var unsyncedData: List<Scan>

            CoroutineScope(Dispatchers.Main).launch {
                unsyncedData = scanDao.getUnsynced()
                for (element in unsyncedData) {
                    sendToOnlineDatabase(
                        element.scan_id,
                        element.insectId,
                        element.latitude,
                        element.longitude,
                        element.altitude,
                        element.timestamp
                    )
                    //scanDao.setSynced(element.scan_id, 1)
                }
                updateDataShown()
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        updateDataShown()

    }

    fun updateDataShown() {
        val scanDao: ScanDao = dbvar.scanDao()
        var unsyncedData: List<Scan>
        CoroutineScope(Dispatchers.Main).launch {
            titles.clear()
            descriptions.clear()
            createdAts.clear()
            unsyncedData = scanDao.getUnsynced()

            for (element in unsyncedData) {
                //sendToOnlineDatabase(element.insectId,element.latitude,element.longitude,element.altitude,element.timestamp)
                titles.add(element.scan_id.toString())
                descriptions.add(element.timestamp)
                createdAts.add(element.insectName)
            }
            // Reverse that latest is up
            titles.reverse()
            descriptions.reverse()
            createdAts.reverse()
            val infoAdapter = HomeNewsListAdapter(
                (activity as MainActivity),
                titles.toTypedArray(),
                descriptions.toTypedArray(),
                createdAts.toTypedArray()
            )
            listView.adapter = infoAdapter
            totalScannedView.text = scanDao.getNumberOfColumns().toString()
            numberTypesView.text = scanDao.getNumberOfTypes().toString()
            numberUnsyncedView.text = scanDao.getNumberOfUnsynced().toString()
        }

    }

    private fun setSynced(scan_id: Int) {
        val scanDao: ScanDao = dbvar.scanDao()
        CoroutineScope(Dispatchers.Main).launch {
            scanDao.setSynced(scan_id, 1)
            updateDataShown()
        }
    }

    private fun sendToOnlineDatabase(
        scanID: Int,
        insectID: String,
        latitude: String,
        longitude: String,
        altitude: String,
        timestamp: String
    ) {

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("insect_id", insectID)
            .add("user_id", "1")
            .add("device_id", "1")
            .add("latitude", latitude)
            .add("longitude", longitude)
            .add("altitude", altitude)
            .add("log_date", timestamp)
            .build()

        val request = Request.Builder()
            .url("http:/85.235.65.8/insert_post.php")
            .post(formBody)
            .build()

        //enqueue: wird i.d.R. sofort aufgerufen, es sei denn es gibt zu viele Requests
        //https://square.github.io/okhttp/4.x/okhttp/okhttp3/-call/enqueue/
        //Standard Timeout: 10s
        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        else {
                            Log.i("respone", "Erfolgreich!")
                            setSynced(scanID)

                        }

                        for ((name, value) in response.headers) {
                            Log.i("response", "$name: $value")
                        }

                        println(response.body!!.string())
                    }
                }
            }
        )

    }


}