package com.sample.trackmylocation.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.sample.trackmylocation.database.AppDatabase
import com.sample.trackmylocation.database.entities.EntityJourneyDetails
import com.sample.trackmylocation.model.LastLocation
import com.sample.trackmylocation.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    fun bitmapFromVector(context:Context, vectorResID:Int): BitmapDescriptor {
        val vectorDrawable= ContextCompat.getDrawable(context,vectorResID)
        vectorDrawable!!.setBounds(0,0, vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    suspend fun saveJourneyData(context: Context, journeyDetails: EntityJourneyDetails) {
        view.showProgressBar()
         val rs = withContext(Dispatchers.IO) {
            val db: AppDatabase = AppDatabase.invoke(context)

            return@withContext db.getJourneyDetailsDao().saveThis(journeyDetails)
        }
        if (rs < 0) {
            view.databaseOperationStatus(false, "Failed to Insert")
        } else {
            view.databaseOperationStatus(true, null)
        }
        view.hideProgressBar()
    }

    interface View {
        fun moveCamera()

        fun databaseOperationStatus(success: Boolean, errorMessage: String?)

        fun showProgressBar()
        fun hideProgressBar()
    }
}