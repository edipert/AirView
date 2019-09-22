package com.android.airview.api.repository

import com.android.airview.api.model.Flight
import com.android.airview.api.model.Result
import com.android.airview.api.service.OpenSkyService
import com.android.airview.di.module.OBSERVER_ON
import com.android.airview.di.module.SUBCRIBER_ON
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Named

class OpenSkyRepository @Inject constructor(
    private val api: OpenSkyService,
    @param:Named(SUBCRIBER_ON) private val subscriberOn: Scheduler,
    @param:Named(OBSERVER_ON) private val observerOn: Scheduler
) {

    private val disposable = CompositeDisposable()

    fun getFlights(
        lomin: Double,
        lamin: Double,
        lomax: Double,
        lamax: Double,
        callback: (Result<Flight>) -> Unit
    ) {
        disposable.add(
            api.getFlights(lomin, lamin, lomax, lamax)
                .subscribeOn(subscriberOn)
                .observeOn(observerOn)
                .subscribe({
                    callback(Result.Success(it))
                }, {
                    callback(Result.Error(Exception(it)))
                })
        )
    }

    fun destroy() {
        disposable.clear()
    }
}