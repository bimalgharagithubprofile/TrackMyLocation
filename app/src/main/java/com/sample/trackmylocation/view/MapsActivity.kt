package com.sample.trackmylocation.view

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.sample.trackmylocation.R
import com.sample.trackmylocation.database.entities.EntityJourneyDetails
import com.sample.trackmylocation.model.LastLocation
import com.sample.trackmylocation.model.LocationEvent
import com.sample.trackmylocation.presenter.MapsActivityPresenter
import com.sample.trackmylocation.services.MyLocationManager
import com.sample.trackmylocation.utils.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapsActivityPresenter.View {

    private lateinit var mGoogleMap: GoogleMap

    lateinit var myLocationManager: MyLocationManager
    lateinit var journeyDetails: EntityJourneyDetails

    private lateinit var presenter: MapsActivityPresenter

    private val DEFAULT_ZOOM = 15f

    private var isStarted = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        myLocationManager = MyLocationManager.getInstance(this)

        presenter = MapsActivityPresenter(this)

        try {
            showProgressBar()
            myLocationManager.startLocationUpdates()
        }catch (e: SecurityException){
            e.printStackTrace()
            toast("Unable to Start Journey")
            finish()
        }

        initClicks()
    }

    private fun initClicks() {
        btn_end.setOnClickListener {
            dialogStopJourney()
        }
    }


    private fun dialogStopJourney() {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(R.string.confirm_end_journey)
            .setCancelable(false)
            .setPositiveButton("Proceed") { dialog, id ->
                dialog.dismiss()

                //stop location service
                myLocationManager.stopLocationUpdates()

                //save this Journey Data into Room DB
                if(this::journeyDetails.isInitialized) {
                    showProgressBar()
                    Coroutines.io(lifecycleScope) {
                        presenter.saveJourneyData(this, journeyDetails)
                    }
                }else {
                    toast("No Journey Details to save !")
                    finish()
                }
            }

        val alert = dialogBuilder.create()
        alert.setTitle("End Journey")
        alert.show()
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
    }

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

        var tmpDuration = "0"
        var tmpDistance = "0"
        var tmpSpeed = "0.0"
        if(endPoc != null) {
            tmpDuration = presenter.calculateDuration(endLocation!!.location.time, startedLocation.location.time)
            tmpDistance = presenter.calculateDistance(startedPoc.latitude, startedPoc.longitude, endPoc.latitude, endPoc.longitude).toString()
            tmpSpeed = presenter.calculateSpeed(endLocation.location.time, startedLocation.location.time, tmpDistance.toDouble())
        }
        //finalise the phase
        val duration = "$tmpDuration min"
        val distance = "$tmpDistance m"
        val speed = "$tmpSpeed m/s"

        withContext(Dispatchers.Main){
            vDuration.text = duration
            vDistance.text = distance
            vSpeed.text = speed
        }

        this.journeyDetails = EntityJourneyDetails(
            startedAt,
            duration,
            distance,
            speed
        )
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

        hideProgressBar()
    }

    override fun databaseOperationStatus(success: Boolean, errorMessage: String?) {
        hideProgressBar()
        if(!errorMessage.isNullOrEmpty() || !success){
                toast(errorMessage!!)
        } else {
            toast("Journey Details Saved")
            finish()
        }
    }

    override fun showProgressBar() {
        vProgressBar.show()
    }

    override fun hideProgressBar() {
        vProgressBar.hide()
    }

    override fun onBackPressed() {
        log("Back Button Clicked")

        dialogStopJourney()
    }

}