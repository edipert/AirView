package com.android.airview.controller.location

import android.location.Location

interface LocationControllerCallback {
    fun onLocationChanged(location: Location, animate: Boolean = false)
}