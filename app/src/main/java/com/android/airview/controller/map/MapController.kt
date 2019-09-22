package com.android.airview.controller.map

import android.content.Context
import android.location.Location
import com.android.airview.R
import com.android.airview.api.model.State
import com.android.airview.controller.location.LocationController
import com.android.airview.controller.location.LocationControllerCallback
import com.android.airview.util.MarkerInfoAdapter
import com.android.airview.util.RefreshTimer
import com.android.airview.util.Util
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapController constructor(
    private val activity: Context
) : OnMapReadyCallback, LocationControllerCallback {

    private var map: GoogleMap? = null

    private var visibleBounds: LatLngBounds? = null

    private lateinit var mapControllerCallback: MapControllerCallback

    private var locationController: LocationController? = null

    fun setMapControllerCallback(mapControllerCallback: MapControllerCallback) {
        this.mapControllerCallback = mapControllerCallback
    }

    override fun onMapReady(map: GoogleMap?) {
        // Create location controller when map is ready
        locationController = LocationController(
            activity,
            this
        ).apply {
            start()
        }

        map?.apply {
            this@MapController.map = this
            mapType = GoogleMap.MAP_TYPE_NORMAL

            // Point device location on map
            isMyLocationEnabled = true

            // Set some ui settings control visibility
            uiSettings.isCompassEnabled = false
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isMyLocationButtonEnabled = false

            // Set a custom info layout adapter for markers
            setInfoWindowAdapter(MarkerInfoAdapter(activity))

            // Listen marker's title click
            setOnInfoWindowClickListener {
                mapControllerCallback.onTitleClicked(it.title)
            }

            // Set camera listener for movements
            setCameraListener()
        }
    }

    // Location controller trigger this method on location updates
    override fun onLocationChanged(location: Location, animate: Boolean) {
        if (animate)
            animateCameraByLatLng(location.latitude, location.longitude)
    }

    // Start timer for 5 seconds periods if map is not moving
    private fun setRefreshTimer() {
        RefreshTimer.startTimer(5) { remainedTime ->
            if (remainedTime == 0) {
                visibleBounds?.let {
                    // Search for flights by last known visible area
                    mapControllerCallback.searchForVisibleArea(it)

                    // Restart timer
                    setRefreshTimer()
                }
            }
        }
    }

    private fun setCameraListener() {
        map?.apply {

            // Stop timer and trigger ui for movement
            setOnCameraMoveStartedListener {
                mapControllerCallback.onCameraMoveStarted()
                RefreshTimer.stopTimer()
            }

            //Start timer when map movement ends, set last known visible area, trigger the ui for movement ending
            setOnCameraIdleListener {
                mapControllerCallback.onCameraIdle()
                visibleBounds = projection.visibleRegion.latLngBounds
                setRefreshTimer()
            }
        }
    }

    // Add markers to map by list of flight state info
    fun addPlaneMarkers(states: List<State>) {
        map?.apply {
            // Clear map
            clear()
            if (states.isNotEmpty()) {
                states.forEach {
                    if (it.latitude != null && it.longitude != null) {
                        addMarker(
                            MarkerOptions()
                                .position(LatLng(it.latitude!!, it.longitude!!))
                                .title(it.originCountry)
                                .icon(
                                    Util.bitmapDescriptorFromVector(
                                        activity,
                                        R.drawable.ic_plane
                                    )
                                )
                        )
                    }
                }
            }
        }
    }

    // Animate the map by lat-lng info
    private fun animateCameraByLatLng(latitude: Double, longitude: Double) {
        map?.apply {
            val googlePlex = CameraPosition.builder()
                .target(LatLng(latitude, longitude)).zoom(8f)
                .bearing(0f)
                .tilt(45f)
                .build()

            animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null)
        }
    }

    // Focus device location
    fun focusUserLocation() {
        locationController?.getUserLocation()?.let {
            animateCameraByLatLng(it.latitude, it.longitude)
        }
    }

    // Get visible area from map
    fun getVisibleArea() = map?.projection?.visibleRegion?.latLngBounds

    // Stop listening location updates
    fun stop() {
        locationController?.stop()
    }

}