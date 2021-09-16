package com.sample.trackmylocation.view

import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.sample.trackmylocation.R
import kotlin.math.abs


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mGoogleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        val sourcePoints: MutableList<LatLng> = ArrayList()

        //just to initiate the Polyline giving some hardcoded values
        sourcePoints.add(LatLng(-35.27801, 149.12958))
        sourcePoints.add(LatLng(-35.28032, 149.12907))
        sourcePoints.add(LatLng(-35.28099, 149.12929))
        sourcePoints.add(LatLng(-35.28144, 149.12984))
        sourcePoints.add(LatLng(-35.28194, 149.13003))
        sourcePoints.add(LatLng(-35.28282, 149.12956))
        sourcePoints.add(LatLng(-35.28302, 149.12881))
        sourcePoints.add(LatLng(-35.28473, 149.12836))

        //addMarker at Starting Point
        addMarker(sourcePoints[0])

        mGoogleMap.setOnMapLoadedCallback {
            var polyLineOptions = PolylineOptions()
            polyLineOptions.addAll(sourcePoints)
            polyLineOptions.width(10f)
            polyLineOptions.color(Color.BLUE)
            mGoogleMap.addPolyline(polyLineOptions)

//            val carPos = LatLng(-35.281120, 149.129721)
            val carPos = sourcePoints.last()
//            addMarker(carPos)
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
                    val snappedToSegment =
                        getMarkerProjectionOnSegment(carPos, segment, mGoogleMap.projection)
                    addMarker(snappedToSegment)
                    break
                }
            }
        }
    }
    private fun getMarkerProjectionOnSegment(carPos: LatLng, segment: List<LatLng>, projection: Projection): LatLng? {
        var markerProjection: LatLng? = null
        val carPosOnScreen: Point = projection.toScreenLocation(carPos)
        val p1: Point = projection.toScreenLocation(segment[0])
        val p2: Point = projection.toScreenLocation(segment[1])
        val carPosOnSegment = Point()
        val denominator: Float = ((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y)).toFloat()
        // p1 and p2 are the same
        if (abs(denominator) <= 1E-10) {
            markerProjection = segment[0]
        } else {
            val t: Float = (carPosOnScreen.x * (p2.x - p1.x) - (p2.x - p1.x) * p1.x
                    + carPosOnScreen.y * (p2.y - p1.y) - (p2.y - p1.y) * p1.y) / denominator
            carPosOnSegment.x = ((p1.x + (p2.x - p1.x) * t)).toInt()
            carPosOnSegment.y = ((p1.y + (p2.y - p1.y) * t)).toInt()
            markerProjection = projection.fromScreenLocation(carPosOnSegment)
        }
        return markerProjection
    }
    private fun addMarker(latLng: LatLng?) {
        latLng?.let {
            mGoogleMap.addMarker(MarkerOptions()
                    .position(it)
            )
        }
    }
}