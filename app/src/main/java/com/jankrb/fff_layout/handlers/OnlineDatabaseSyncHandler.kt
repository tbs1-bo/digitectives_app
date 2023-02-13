package com.jankrb.fff_layout.handlers

import android.util.Log
import com.jankrb.fff_layout.MainActivity
import com.jankrb.fff_layout.ui.HomeFragment
import okhttp3.*
import java.io.IOException
import java.security.SecureRandom

class OnlineDatabaseSyncHandler(val homeFragment: HomeFragment) {

    fun sendToDatabase(
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
            .add("device_id", getOrCreateLocalDeviceKey())
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
                        else {
                            Log.i("respone", "Erfolgreich!")
                            homeFragment.setSynced(scanID, 1)

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

    fun getOrCreateLocalDeviceKey() : String {
        val preferenceKey = "device_id"
        var deviceID = MainActivity.preferences.getString(preferenceKey, null)

        if(deviceID != null) return deviceID

        // Generate and save new device id
        deviceID = generateDeviceId(12)
        MainActivity.preferences.edit().putString(preferenceKey, deviceID)
            .apply()

        return deviceID
    }

    private fun generateDeviceId(length: Int) : String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIKLMNOPQRSTUVWXYZ1234567890!?="
        val secureRandom = SecureRandom()

        var deviceID = ""
        for(i in 0 until length) {
            deviceID += chars[secureRandom.nextInt(chars.length)]
        }
        return deviceID
    }
}