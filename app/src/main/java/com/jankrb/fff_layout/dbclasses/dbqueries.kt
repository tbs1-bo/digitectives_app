package com.jankrb.fff_layout.dbclasses

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object dbqueries {
    //Koordinaten in DB Eintragen
    fun addToDatabase(latitudeValue: String, longitudeValue: String, altitudeValue: String){
        //add location to sqlite db
        val scanDao : ScanDao = dbvar.scanDao()
        CoroutineScope(Dispatchers.Main).launch{
            scanDao.insertAll(Scan(latitude = latitudeValue, longitude=longitudeValue, altitude = altitudeValue))
            //Log.d("INFO","Aufruf erfolgreich")
        }

    }

/*    fun getDatabaseEntries(): String {
        val commentDao : ScanDao = dbvar.scanDao()
        var returnValue = MutableLiveData<String>()
        CoroutineScope(Dispatchers.Main).launch{
            val returnValueData = commentDao.getAll().toString()
            returnValue.postValue(returnValueData)
        }
        return returnValue.toString()
    }*/
}