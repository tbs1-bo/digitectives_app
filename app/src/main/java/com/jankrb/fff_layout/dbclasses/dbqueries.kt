package com.jankrb.fff_layout.dbclasses

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object dbqueries {

    fun addToLocalDatabase(insectNameValue: String, insectIDValue: String, latitudeValue: String, longitudeValue: String, altitudeValue: String, timestampValue: String, syncedValue: Int) {
        val scanDao : ScanDao = dbvar.scanDao()
        CoroutineScope(Dispatchers.Main).launch{
            scanDao.insertAll(Scan(insectName = insectNameValue, insectId = insectIDValue,latitude = latitudeValue, longitude=longitudeValue, altitude = altitudeValue, timestamp = timestampValue, synced = syncedValue))
        }
    }



}