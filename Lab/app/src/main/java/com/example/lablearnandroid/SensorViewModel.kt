package com.example.lablearnandroid

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorViewModel : ViewModel() {

    private val _accelerometerData = MutableStateFlow(floatArrayOf(0f, 0f, 0f))
    val accelerometerData: StateFlow<FloatArray> = _accelerometerData.asStateFlow()

    private val _locationData = MutableStateFlow(Pair(0.0, 0.0))
    val locationData: StateFlow<Pair<Double, Double>> = _locationData.asStateFlow()

    fun updateAccelerometer(values: FloatArray) {
        _accelerometerData.value = values
    }

    fun updateLocation(lat: Double, lng: Double) {
        _locationData.value = Pair(lat, lng)
    }
}
