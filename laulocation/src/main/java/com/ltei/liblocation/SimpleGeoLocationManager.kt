package com.ltei.laulocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.annotation.RequiresPermission

class SimpleGeoLocationManager
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
constructor(private val context: Context) : GeoLocationManager {


    private var locationManager: LocationManager? = null
    private var currentLocationVar: Location? = null
    private var currentLocationProvider: String? = null
    private val listeners = ArrayList<GeoLocationManagerListener>()

    private val mNetworkProviderUpdateCd = com.ltei.ljuutils.Cooldown(1000)

    private val mLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocationVar = location
            listeners.forEach { it.onStateChanged() }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {
            // Re-Check to track the best locationProvider
            if (provider == LocationManager.GPS_PROVIDER) {
                if (currentLocationProvider != LocationManager.GPS_PROVIDER) {
                    stopTracking()
                    startTracking()
                }
            } else if (provider == LocationManager.NETWORK_PROVIDER) {
                if (currentLocationProvider != LocationManager.NETWORK_PROVIDER) {
                    stopTracking()
                    startTracking()
                }
            }
            listeners.forEach { it.onStateChanged() }
        }

        override fun onProviderDisabled(provider: String) {
            // Our provider has been disabled, get another one
            if (provider == currentLocationProvider) {
                if (provider == LocationManager.NETWORK_PROVIDER) {
                    mNetworkProviderUpdateCd.tryActivate {
                        stopTracking()
                        startTracking()
                    }
                } else {
                    stopTracking()
                    startTracking()
                }
            }
            listeners.forEach { it.onStateChanged() }
        }
    }

    @SuppressLint("MissingPermission") // All permissions are required by constructor
    override fun startTracking() {
        if (locationManager == null) {
            // Select a location provider (GPS, NETWORK)
            currentLocationProvider = null

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            this.locationManager = locationManager

            var noProvider = false
            when {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                    currentLocationProvider = LocationManager.GPS_PROVIDER
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10f, mLocationListener)
                    currentLocationVar = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
                locationManager.allProviders.contains(LocationManager.NETWORK_PROVIDER) -> {
                    currentLocationProvider = LocationManager.NETWORK_PROVIDER
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10f, mLocationListener)
                    currentLocationVar = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
                else -> noProvider = true
            }

            // In case the previous location provider returns null, try to update the location
            if (!noProvider && currentLocationVar == null) {
                val providers = locationManager.getProviders(true)
                for (i in providers.indices.reversed()) {
                    locationManager.requestLocationUpdates(providers[i], 0, 0f, mLocationListener)
                    currentLocationVar = locationManager.getLastKnownLocation(providers[i])
                    if (currentLocationVar != null) break
                }
            }
        }
    }

    @SuppressLint("MissingPermission") // All permissions are required by constructor
    override fun stopTracking() {
        locationManager?.let {
            it.removeUpdates(this.mLocationListener)
            locationManager = null
            currentLocationProvider = null
        }
    }

    override fun getCurrentLocation(): Location? {
        return currentLocationVar
    }

    override fun addListener(listener: GeoLocationManagerListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: GeoLocationManagerListener) {
        listeners.remove(listener)
    }

}
