package com.sample.trackmylocation.presenter

import android.location.Location
import com.sample.trackmylocation.model.LastLocation
import com.sample.trackmylocation.utils.log
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

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

    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Int {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lng2 - lng1)
        val a = (sin(dLat / 2) * sin(dLat / 2)
                + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2)))
        val c = 2 * asin(sqrt(a))
        return (6371000 * c).roundToInt()
    }

    fun calculateSpeed(currentTime: Long, oldTime: Long, distance: Double): String {
        val timeDifferent: Double = (currentTime - oldTime).toDouble()
        val speed: Double = distance / timeDifferent

        val shorten = speed.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()

        return shorten.toString()
    }

    fun calculateDuration(currentTime: Long, oldTime: Long): String {
        val diff: Long = (currentTime - oldTime)
        val seconds = diff / 1000
        val minutes = seconds / 60

        return minutes.toString()
    }

    fun calculateStartedAt(time: Long, format: String): String {
        val date = Date(time)
        return date.toString(format)
    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    interface View {
        fun moveCamera()

        fun showProgressBar()
        fun hideProgressBar()
    }
}