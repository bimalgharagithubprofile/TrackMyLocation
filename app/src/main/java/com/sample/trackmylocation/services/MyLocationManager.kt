package com.sample.trackmylocation.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.MainThread
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.sample.trackmylocation.utils.log
import java.util.concurrent.TimeUnit


class MyLocationManager private constructor(private val context: Context) {


    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest = LocationRequest().apply {
        interval = TimeUnit.SECONDS.toMillis(5)
        fastestInterval = TimeUnit.SECONDS.toMillis(3)
        maxWaitTime = TimeUnit.SECONDS.toMillis(30)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 0f //distance in meters to trigger update
    }

    private val locationUpdatePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, LocationUpdatesBroadcastReceiver::class.java)
        intent.action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    @Throws(SecurityException::class)
    @MainThread
    fun startLocationUpdates() {
        log("startLocationUpdates()")
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationUpdatePendingIntent)
        } catch (permissionRevoked: SecurityException) {
            log("Location permission revoked; details: $permissionRevoked")
            throw permissionRevoked
        }
    }

    @MainThread
    fun stopLocationUpdates() {
        log("stopLocationUpdates()")
        fusedLocationClient.removeLocationUpdates(locationUpdatePendingIntent)
    }

    companion object {
        @Volatile private var INSTANCE: MyLocationManager? = null

        fun getInstance(context: Context): MyLocationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MyLocationManager(context).also { INSTANCE = it }
            }
        }
    }
}
