package com.jankrb.fff_layout.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jankrb.fff_layout.MainActivity
import com.jankrb.fff_layout.R
import com.jankrb.fff_layout.dbclasses.Scan
import com.jankrb.fff_layout.dbclasses.ScanDao
import com.jankrb.fff_layout.dbclasses.dbvar
import com.jankrb.fff_layout.handlers.OnlineDatabaseSyncHandler
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


        //Lange auf Eintrag eines gescannten Insekts -> Löschen
        listView.onItemLongClickListener =
            OnItemLongClickListener { arg0, arg1, pos, id ->
                val dialogBuilder = AlertDialog.Builder(this.context)
                val listItem: Any = listView.getItemAtPosition(pos)

                dialogBuilder.setMessage("Eintrag löschen?")
                    .setCancelable(false)
                    .setPositiveButton("Ja", DialogInterface.OnClickListener {
                            dialog, id -> setSynced(listItem.toString().toInt(),3)
                    })
                    // negative button text and action
                    .setNegativeButton("Nein", DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })


                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("Warnung:")
                // show alert dialog
                alert.show()

                Log.i("long clicked", "pos: $pos")

                Log.i("long clicked", listItem.toString())

                true
            }


        //Synchronisieren Button
        sync_btn.setOnClickListener {
            val scanDao: ScanDao = dbvar.scanDao()
            var unsyncedData: List<Scan>

            CoroutineScope(Dispatchers.Main).launch {
                unsyncedData = scanDao.getUnsynced()
                val onlineDatabaseSyncHandler = OnlineDatabaseSyncHandler(this@HomeFragment)
                for (element in unsyncedData) {
                    onlineDatabaseSyncHandler.sendToDatabase(
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
            totalScannedView.text = scanDao.getNumberOfInsects().toString()
            numberTypesView.text = scanDao.getNumberOfTypes().toString()
            numberUnsyncedView.text = scanDao.getNumberOfUnsynced().toString()

        }

    }

    fun setSynced(scan_id: Int, value: Int) {
        val scanDao: ScanDao = dbvar.scanDao()
        CoroutineScope(Dispatchers.Main).launch {
            scanDao.setSynced(scan_id, value)
            updateDataShown()
        }
    }
}
