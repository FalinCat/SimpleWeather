package com.example.falin.simpleweather.Controller

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log

class LocationController(private val context: Context) {
    private val TAG = "APPTAG"

    private lateinit var locationManager: LocationManager
    // Public
    var longitude: Double = 0.0
    var latitude: Double = 0.0


    private fun requestForUpdate(){
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (e: Exception) {
            Log.i(TAG, "Error in requestForUpdate\n${e.message}")
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.i(TAG, "onLocationChanged")
            Log.i(TAG, "Longitude is " + location.longitude.toString())
            Log.i(TAG, "Latitude is " +  location.latitude.toString())

            longitude =location.longitude
            latitude =location.latitude
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
        override fun onProviderEnabled(p0: String?) {}
        override fun onProviderDisabled(p0: String?) {}

    }

    init {
        requestForUpdate()
    }


}