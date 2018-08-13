package com.example.falin.simpleweather.Controller

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import com.example.falin.simpleweather.MainActivity

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
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)
//            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error in requestForUpdate\n${e.message}")
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            // Внезапно тут переход на основную Activity
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("LOCATION", location)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(context, intent, null)
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
        override fun onProviderEnabled(p0: String?) {}
        override fun onProviderDisabled(p0: String?) {}

    }

    init {
        requestForUpdate()
    }


}