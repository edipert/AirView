package com.android.airview.api.model

data class Flight(
    var time: Long = 0,
    var states: MutableList<State> = mutableListOf()
)