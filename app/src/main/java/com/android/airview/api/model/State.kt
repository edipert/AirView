package com.android.airview.api.model

data class State(
    var icao24: String?,
    var callsign: String?,
    var originCountry: String?,
    var timePosition: Int?,
    var lastContact: Int?,
    var longitude: Double?,
    var latitude: Double?,
    var baroAltitude: Float?,
    var onGround: Boolean?,
    var velocity: Float?,
    var trueTrack: Float?,
    var verticalRate: Float?,
    var sensors: List<Int>?,
    var geoAltitude: Float?,
    var squawk: String?,
    var spi: Boolean?,
    var positionSource: Int?
)