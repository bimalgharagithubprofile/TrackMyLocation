package com.sample.trackmylocation.view

import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.sample.trackmylocation.R
import com.sample.trackmylocation.model.LastLocation
import com.sample.trackmylocation.model.LocationEvent
import com.sample.trackmylocation.presenter.MapsActivityPresenter
import com.sample.trackmylocation.services.LocationService
import com.sample.trackmylocation.utils.Coroutines
import com.sample.trackmylocation.utils.hide
import com.sample.trackmylocation.utils.log
import com.sample.trackmylocation.utils.show
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Math.abs
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapsActivityPresenter.View {

    private lateinit var mGoogleMap: GoogleMap

    lateinit var presenter: MapsActivityPresenter

//    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val DEFAULT_ZOOM = 15f



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        presenter = MapsActivityPresenter(this)

        ContextCompat.startForegroundService(this, Intent(this, LocationService::class.java))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LocationEvent?) {
        event?.let {
            log("Considering New Location: ${it.location.latitude}, ${it.location.longitude}")
            presenter.setNewLocation(it.location)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

//        presenter.getDeviceLocation(this)




        /*mGoogleMap.setOnMapLoadedCallback {
            val sourcePoints: MutableList<LatLng> = ArrayList()
            sourcePoints.add(LatLng(-35.27801, 149.12958))

            var polyLineOptions = PolylineOptions()
//            polyLineOptions.addAll(sourcePoints)
//            polyLineOptions.width(10f)
//            polyLineOptions.color(Color.BLUE)
//            mGoogleMap.addPolyline(polyLineOptions)

            val carPos = sourcePoints.last()
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(carPos, 15f))
            for (i in 0 until sourcePoints.size - 1) {
                val segmentP1 = sourcePoints[i]
                val segmentP2 = sourcePoints[i + 1]
                val segment: MutableList<LatLng> = ArrayList(2)
                segment.add(segmentP1)
                segment.add(segmentP2)
                if (PolyUtil.isLocationOnPath(carPos, segment, true, 30.0)) {
                    polyLineOptions = PolylineOptions()
                    polyLineOptions.addAll(segment)
                    polyLineOptions.width(10f)
                    polyLineOptions.color(Color.RED)
                    mGoogleMap.addPolyline(polyLineOptions)
//                    val snappedToSegment = getMarkerProjectionOnSegment(carPos, segment, mGoogleMap.projection)
//                    addMarker(snappedToSegment)
                    break
                }
            }
        }*/
    }
    /*private fun getMarkerProjectionOnSegment(carPos: LatLng, segment: List<LatLng>, projection: Projection): LatLng? {
        var markerProjection: LatLng? = null
        val carPosOnScreen: Point = projection.toScreenLocation(carPos)
        val p1: Point = projection.toScreenLocation(segment[0])
        val p2: Point = projection.toScreenLocation(segment[1])
        val carPosOnSegment = Point()
        val denominator: Float = ((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y)).toFloat()
        // p1 and p2 are the same
        if (kotlin.math.abs(denominator) <= 1E-10) {
            markerProjection = segment[0]
        } else {
            val t: Float = (carPosOnScreen.x * (p2.x - p1.x) - (p2.x - p1.x) * p1.x
                    + carPosOnScreen.y * (p2.y - p1.y) - (p2.y - p1.y) * p1.y) / denominator
            carPosOnSegment.x = ((p1.x + (p2.x - p1.x) * t)).toInt()
            carPosOnSegment.y = ((p1.y + (p2.y - p1.y) * t)).toInt()
            markerProjection = projection.fromScreenLocation(carPosOnSegment)
        }
        return markerProjection
    }*/
    /*private fun addMarker(latLng: LatLng?) {
        latLng?.let {

        }
    }*/


    /*override fun initCamera(location: Location) {
        val currentPosition = LatLng(location.latitude, location.longitude)
        mGoogleMap.addMarker(
            MarkerOptions()
                .position(currentPosition)
        )
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, DEFAULT_ZOOM))
    }
    override fun moveCamera(location: Location) {
        val currentPosition = LatLng(location.latitude, location.longitude)
        mGoogleMap.addMarker(
            MarkerOptions()
                .position(currentPosition)
        )
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, DEFAULT_ZOOM))
    }*/
    private fun addMarkers(startedLocation: LastLocation, endLocation: LastLocation?) {
        //started Marker
        mGoogleMap.addMarker(
            MarkerOptions()
                .position(LatLng(startedLocation.location.latitude, startedLocation.location.longitude))
        )
        //end Marker
        if(endLocation != null){
            mGoogleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(endLocation.location.latitude, endLocation.location.longitude))
            )
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(endLocation.location.latitude, endLocation.location.longitude), DEFAULT_ZOOM))
        } else {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(startedLocation.location.latitude, startedLocation.location.longitude), DEFAULT_ZOOM))
        }
    }
    private suspend fun addPolyline() {
        val sourcePoints: MutableList<LatLng> = ArrayList()
        for(location in presenter.lastLocation)
            sourcePoints.add(LatLng(location.location.latitude, location.location.longitude))

        val polyLineOptions = PolylineOptions()
        polyLineOptions.addAll(sourcePoints)
        polyLineOptions.width(13f)
        polyLineOptions.color(Color.BLUE)

        withContext(Dispatchers.Main) {
            mGoogleMap.addPolyline(polyLineOptions)
        }
    }
    private suspend fun addJourneyDetails(startedLocation: LastLocation, endLocation: LastLocation?) {
        val startedPoc = LatLng(startedLocation.location.latitude, startedLocation.location.longitude)
        var endPoc: LatLng?=null
        if(endLocation!=null)
            endPoc = LatLng(endLocation.location.latitude, endLocation.location.longitude)


        val startedAt = presenter.calculateStartedAt(startedLocation.location.time, "hh:mm a")
        withContext(Dispatchers.Main){
            vStartedAt.text = startedAt
        }

        if(endPoc == null){
            withContext(Dispatchers.Main) {
                vDuration.text = "0 min"
                vDistance.text = "0 m"
                vSpeed.text = "0.0 m/s"
            }
        } else {
            val duration = presenter.calculateDuration(endLocation!!.location.time, startedLocation.location.time)

            val distance = presenter.calculateDistance(startedPoc.latitude, startedPoc.longitude, endPoc.latitude, endPoc.longitude)

            val speed = presenter.calculateSpeed(endLocation!!.location.time, startedLocation.location.time, distance.toDouble())

            withContext(Dispatchers.Main){
                vDuration.text = "$duration min"
                vDistance.text = "$distance m"
                vSpeed.text = "$speed m/s"
            }
        }
    }

    override fun moveCamera() = Coroutines.io(lifecycleScope){
        var isInitial = true
        if(presenter.lastLocation.size > 1)
            isInitial = false

        val startedLocation = presenter.lastLocation.first()
        var endLocation: LastLocation?=null
        if(!isInitial)
            endLocation = presenter.lastLocation.last()

        withContext(Dispatchers.Main) {
            mGoogleMap.clear()
            addMarkers(startedLocation, endLocation)
        }

        if(!isInitial)
            addPolyline()

        addJourneyDetails(startedLocation, endLocation)
    }

    override fun showProgressBar() {
        vProgressBar.show()
    }

    override fun hideProgressBar() {
        vProgressBar.hide()
    }
}