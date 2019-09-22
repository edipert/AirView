package com.android.airview.controller.map

import android.location.Location
import com.google.android.gms.maps.model.LatLngBounds

interface MapControllerCallback {
    fun searchForVisibleArea(visibleBounds: LatLngBounds)
    fun onTitleClicked(title: String)
    fun onCameraMoveStarted()
    fun onCameraIdle()
}