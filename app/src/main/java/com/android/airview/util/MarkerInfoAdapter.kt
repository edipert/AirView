package com.android.airview.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.android.airview.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.popup_marker.view.*


class MarkerInfoAdapter(
    context: Context
) : GoogleMap.InfoWindowAdapter {

    private val infoContent: View =
        LayoutInflater.from(context).inflate(R.layout.popup_marker, null)

    // Set a custom layout for marker's info window
    override fun getInfoContents(marker: Marker?): View {
        infoContent.txt_title.text = marker?.title
        return infoContent
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }
}