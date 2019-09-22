package com.android.airview.api.service

import com.android.airview.api.model.Flight
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenSkyService {
    @GET("states/all")
    fun getFlights(
        @Query("lomin") lomin: Double,
        @Query("lamin") lamin: Double,
        @Query("lomax") lomax: Double,
        @Query("lamax") lamax: Double
    ): Single<Flight>
}