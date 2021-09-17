package com.sample.trackmylocation.presenter

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sample.trackmylocation.model.LastLocation
import com.sample.trackmylocation.view.HomeActivity.Companion.mLocationPermissionGranted

class MapsActivityPresenter(private var view: View) {

    var lastLocation: MutableList<LastLocation> = ArrayList()


    fun setNewLocation(location: Location?) {
        location?.let {
            lastLocation.add(LastLocation(it))

            /*if(lastLocation.size == 1)
                view.initCamera(it)
            else
                view.moveCamera(it)*/

            view.moveCamera()
        }
    }

    /*fun getDeviceLocation(
        context: Context
    ) {
        try {
            view.showProgressBar()

            val mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let {
                            setNewLocation(task.result)
                            view.moveCamera(task.result)
                        }
                    } else {
                        Log.d("chk", "Current location is null. Using defaults.")
                        Log.e("chk", "Exception: %s", task.exception)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message!!)
        } finally {
            view.hideProgressBar()
        }
    }*/


    interface View {
//        fun initCamera(location: Location)
//        fun moveCamera(location: Location)
        fun moveCamera()

        fun showProgressBar()
        fun hideProgressBar()
    }
}