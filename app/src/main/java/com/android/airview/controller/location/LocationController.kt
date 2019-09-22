package com.android.airview.controller.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.airview.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class LocationController(
    private val activity: Context,
    private val locationControllerCallback: LocationControllerCallback
) :
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var userLocation: Location? = null

    private var googleApiClient: GoogleApiClient? = null

    private var locationRequest: LocationRequest? = null

    init {
        setGoogleApiClient()
    }

    fun start() {
        googleApiClient?.connect()
    }

    private fun setGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(activity)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build()

    }

    // First check permission on connection, if it is granted get last known location of device an focus on map
    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient).apply {
            locationControllerCallback.onLocationChanged(this, true)
        }

        //Request for location updates
        startLocationUpdates()
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location?) {
        // If location is not null set location
        location?.let { it ->
            userLocation = it.apply {
                locationControllerCallback.onLocationChanged(this)
            }
        }
    }

    // First check permission and request for location updates
    private fun startLocationUpdates() {
        locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL)

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                activity,
                activity.getString(R.string.label_location_warning),
                Toast.LENGTH_SHORT
            ).show()
        }

        // Register a listener for location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient,
            locationRequest,
            this
        )
    }

    fun getUserLocation() = userLocation

    //Stop listening location updates
    fun stop() {
        if (googleApiClient != null && googleApiClient?.isConnected == true) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient,
                this@LocationController
            )
            googleApiClient?.disconnect()
        }
    }

    companion object {
        private const val UPDATE_INTERVAL = 2 * 1000L
        private const val FASTEST_INTERVAL = 20000L
    }

}