package com.ltei.laulocation

import android.location.Location

interface GeoLocationManager {

    fun startTracking()
    fun stopTracking()

    fun getCurrentLocation(): Location?

    fun addListener(listener: GeoLocationManagerListener)
    fun removeListener(listener: GeoLocationManagerListener)

}