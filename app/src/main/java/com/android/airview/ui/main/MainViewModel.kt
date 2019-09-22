package com.android.airview.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.airview.api.model.Result
import com.android.airview.api.model.State
import com.android.airview.api.repository.OpenSkyRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: OpenSkyRepository
) : ViewModel() {

    var flights = MutableLiveData<List<State>>()
    val error = MutableLiveData<String>()
    val filter = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()

    // Get flight info by coordinates
    fun getFlights(
        lomin: Double,
        lamin: Double,
        lomax: Double,
        lamax: Double
    ) {
        repository.getFlights(lomin, lamin, lomax, lamax) {
            when (it) {
                is Result.Success -> {
                    loading.value = false
                    flights.value = if (filter.value.isNullOrEmpty()) {
                        it.response.states
                    } else {
                        it.response.states.filter { state -> state.originCountry == filter.value }
                    }
                }
                is Result.Error -> {
                    loading.value = false
                    error.postValue(it.exception.message)
                }
            }
        }
    }

    fun filter(filterKey: String) {
        filter.value = filterKey
        flights.value?.let { list ->
            flights.postValue(list.filter { it.originCountry == filterKey })
        }
    }

    fun clearFilter() {
        filter.value = null
    }

    fun destroy() {
        repository.destroy()
    }
}
