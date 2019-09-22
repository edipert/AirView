package com.android.airview.controller.map

import com.google.android.gms.maps.model.LatLngBounds

interface MapControllerCallback {
    fun onMapReady()
    fun searchForVisibleArea(visibleBounds: LatLngBounds)
    fun onTitleClicked(title: String)
    fun onCameraMoveStarted()
    fun onCameraIdle()
}