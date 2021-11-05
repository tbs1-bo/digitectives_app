package com.jankrb.fff_layout.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jankrb.fff_layout.R
import com.jankrb.fff_layout.dbclasses.Scan
import com.jankrb.fff_layout.dbclasses.ScanDao
import com.jankrb.fff_layout.dbclasses.dbqueries.sendToOnlineDatabase
import com.jankrb.fff_layout.dbclasses.dbvar
import com.jankrb.fff_layout.objects.PrivateSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val createdAts: MutableList<String> = mutableListOf()
    private val titles: MutableList<String> = mutableListOf()
    private val descriptions: MutableList<String> = mutableListOf()
    private lateinit var scanView: TextView //um auf Objekt aus anderen Methoden zugreifen zu können
    private lateinit var totalScannedView: TextView
    private lateinit var numberTypesView: TextView
    private lateinit var numberUnsyncedView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.home_fragment, container, false)

        // Home Sync Btn
        val sync_btn: Button = root.findViewById(R.id.sync_data_btn)

        scanView = root.findViewById(R.id.home_recent_informations)
        totalScannedView = root.findViewById(R.id.stats_box_self_value)
        numberTypesView = root.findViewById(R.id.stats_box_types_value)
        numberUnsyncedView = root.findViewById(R.id.stats_box_unsynced_value)

        val gd = GradientDrawable(
            GradientDrawable.Orientation.BL_TR, intArrayOf(Color.parseColor(PrivateSettings.gradientStart), Color.parseColor(PrivateSettings.gradientStop)))
        gd.cornerRadius = 50f
        sync_btn.background = gd

        // Recent Information ScrollView
        // Test Element
/*        titles.add("Title 1")
        titles.add("Title 2")
        titles.add("Title 3")
        titles.add("Title 4")
        descriptions.add("Description 1")
        descriptions.add("Description 2")
        descriptions.add("Description 3")
        descriptions.add("Description 4")
        createdAts.add("Created At 1")
        createdAts.add("Created At 2")
        createdAts.add("Created At 3")
        createdAts.add("Created At 4")*/

        sync_btn.setOnClickListener {
            val scanDao: ScanDao = dbvar.scanDao()
            var unsentData: List<Scan>

            CoroutineScope(Dispatchers.Main).launch {
                unsentData = scanDao.getUnsynced()
                for (element in unsentData) {
                    sendToOnlineDatabase(element.insectId,element.latitude,element.longitude,element.altitude,element.timestamp)
                    scanDao.setSynced(element.scan_id, 1)
                }
                updateDataShown()
            }
        }

        // Reverse that latest is up
/*        titles.reverse()
        descriptions.reverse()
        createdAts.reverse()

        // Load ScrollView
        val infoAdapter = HomeNewsListAdapter((activity as MainActivity), titles.toTypedArray(), descriptions.toTypedArray(), createdAts.toTypedArray())
        val listView = root.findViewById<ListView>(R.id.home_recent_informations)
        listView.adapter = infoAdapter*/

        return root
    }

    override fun onResume() {
        super.onResume()
        updateDataShown()

        }

    fun updateDataShown() {
        val scanDao: ScanDao = dbvar.scanDao()
        CoroutineScope(Dispatchers.Main).launch {
            scanView.text = scanDao.getUnsynced().toString()
            totalScannedView.text = scanDao.getNumberOfColumns().toString()
            numberTypesView.text = scanDao.getNumberOfTypes().toString()
            numberUnsyncedView.text = scanDao.getNumberOfUnsynced().toString()
        }

    }


}