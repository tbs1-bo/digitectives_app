package com.jankrb.fff_layout.dbclasses

import android.util.Log
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

object dbqueries {
    //Koordinaten in DB Eintragen
    fun addToLocalDatabase(insectIDValue: String, latitudeValue: String, longitudeValue: String, altitudeValue: String, timestampValue: String, syncedValue: Int) {
        //add location to sqlite db
        val scanDao : ScanDao = dbvar.scanDao()
        CoroutineScope(Dispatchers.Main).launch{
            scanDao.insertAll(Scan(insectId = insectIDValue,latitude = latitudeValue, longitude=longitudeValue, altitude = altitudeValue, timestamp = timestampValue, synced = syncedValue))
        }



    }

    fun sendToOnlineDatabase(insectID: String, latitude: String, longitude: String, altitude: String, timestamp: String){

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

        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        for ((name, value) in response.headers) {
                            Log.i("response","$name: $value")
                        }

                        println(response.body!!.string())
                    }
                }
            },
        )

    }

}