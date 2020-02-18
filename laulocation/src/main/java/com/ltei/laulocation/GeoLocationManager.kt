package com.ltei.laulocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
import com.ltei.lauutils.PermissionsManager
import com.ltei.ljuutils.misc.Cooldown

class GeoLocationManager(
    private val context: Context,
    private val permissionsManager: PermissionsManager
) {

    private val listeners = ArrayList<Listener>()

    private var locationManager: LocationManager? = null
    private val locationListener = MLocationListener()
    var currentLocationProvider: String? = null
        private set
    private val networkProviderUpdateCooldown = Cooldown(1000)

    private var mCurrentLocation: Location? = null

    fun getCurrentLocationIfKnown(): Location? = mCurrentLocation

    fun getCurrentLocation(callback: (Location) -> Unit = {}) {
        mCurrentLocation?.let(callback)
        return startTracking { callback.invoke(mCurrentLocation!!) }
    }

    @SuppressLint("MissingPermission")
    fun startTracking(callback: () -> Unit = {}) {
        return if (locationManager == null) {
            permissionsManager.assertPermission(Manifest.permission.ACCESS_FINE_LOCATION) { permissionSuccess ->
                if (permissionSuccess) {
                    currentLocationProvider = null
                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    this.locationManager = locationManager

                    when {
                        locationManager.isProviderEnabled(GPS_PROVIDER) -> {
                            currentLocationProvider = GPS_PROVIDER
                            locationManager.requestLocationUpdates(GPS_PROVIDER, 100, 10f, locationListener)
                            mCurrentLocation = locationManager.getLastKnownLocation(GPS_PROVIDER)
                        }
                        locationManager.allProviders.contains(NETWORK_PROVIDER) -> {
                            currentLocationProvider = NETWORK_PROVIDER
                            locationManager.requestLocationUpdates(NETWORK_PROVIDER, 100, 10f, locationListener)
                            mCurrentLocation = locationManager.getLastKnownLocation(NETWORK_PROVIDER)
                        }
                        else -> throw NoEnabledGPSProviderException("No enabled GPS provider found.")
                    }

                    // In case the previous location provider returns null, try to update the location
                    if (mCurrentLocation == null) {
                        val providers = locationManager.getProviders(true)
                        if (providers.isEmpty())
                            throw NoEnabledGPSProviderException("No enabled GPS provider found.")
                        for (i in providers.indices.reversed()) {
                            locationManager.requestLocationUpdates(providers[i], 0, 0f, locationListener)
                            mCurrentLocation = locationManager.getLastKnownLocation(providers[i])
                            if (mCurrentLocation != null) break
                        }
                    }
                }

                callback.invoke()
            }
        } else {
            callback.invoke()
        }
    }

    @SuppressLint("MissingPermission")
    fun stopTracking() {
        if (locationManager != null && permissionsManager.areGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationManager?.removeUpdates(locationListener)
        }
        locationManager = null
        currentLocationProvider = null
        mCurrentLocation = null
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private inner class MLocationListener : LocationListener {
        override fun onLocationChanged(location: Location?) {
            mCurrentLocation = location
            listeners.forEach { it.onStateChanged() }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) = Unit

        override fun onProviderEnabled(provider: String?) {
            // Re-Check to track the best locationProvider
            if (provider == GPS_PROVIDER) {
                if (currentLocationProvider != GPS_PROVIDER) {
                    stopTracking()
                    startTracking()
                }
            } else if (provider == NETWORK_PROVIDER) {
                if (currentLocationProvider != NETWORK_PROVIDER) {
                    stopTracking()
                    startTracking()
                }
            }
            listeners.forEach { it.onStateChanged() }
        }

        override fun onProviderDisabled(provider: String?) {
            // Our provider has been disabled, get another one
            if (provider == currentLocationProvider) {
                if (provider == NETWORK_PROVIDER) {
                    networkProviderUpdateCooldown.tryActivate {
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

    interface Listener {
        fun onStateChanged()
    }

}