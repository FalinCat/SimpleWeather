package com.example.falin.simpleweather

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.example.falin.simpleweather.Controller.LocationController

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        checkLocation()

        val location = LocationController(this)
    }

    // Проверяем включен ли GPS или интернет
    private fun checkLocation(): Boolean {
        if (!isLocationEnabled()) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Enable Location")
                    .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app")
                    .setPositiveButton("Location Settings") { _, _ ->
                        val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(myIntent)
                    }
                    .setNegativeButton("Cancel") { _, _ -> finish() }
            dialog.show()
        }

        return isLocationEnabled()
    }

    private fun isLocationEnabled(): Boolean {
        val isGpsEnabled = (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return isGpsEnabled || isNetworkEnabled
    }
}
