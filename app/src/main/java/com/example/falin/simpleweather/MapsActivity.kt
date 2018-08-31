package com.example.falin.simpleweather

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var chooseCoordinats = false
    private val TAG = "APPTAG"
    private var exit: Boolean? = false
    private var APP_PREFERENCE = "appPrefs"
    private lateinit var prefs: SharedPreferences
    private lateinit var userPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val isFromStart = intent.getBooleanExtra("fromStartActivity", false)

        prefs = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
        userPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (!isFromStart) {
            prefs.edit()
                    .clear()
                    .apply()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        var location: LatLng

        mMap.setOnMapClickListener {
            mMap.clear()
            location = LatLng(it.latitude, it.longitude)
            try {
                val gcd = Geocoder(this, Locale.getDefault())
                val addresses = gcd.getFromLocation(it.latitude, it.longitude, 1)
                if (addresses.size > 0) {
                    mMap.addMarker(MarkerOptions().position(location).title(addresses[0].locality))
                    Log.d(LOG_TAG, addresses[0].locality.orEmpty())

                } else {
                    mMap.addMarker(MarkerOptions().position(location))
                    Log.d(LOG_TAG, "Не удалось получить название местности")
                }
            } catch (e: Exception) {
                Log.d(LOG_TAG, "Ошибка. Не удалось получить название местности")
                mMap.addMarker(MarkerOptions().position(location))
            }
        }

        mMap.setOnMarkerClickListener {

            if (chooseCoordinats) {
                val markerLocation = Location("")
                markerLocation.latitude = it.position.latitude
                markerLocation.longitude = it.position.longitude

                if (userPrefs.getBoolean("useSavedLocation", true)) {
                    prefs.edit()
                            .putString("LOCATION", "${it.position.latitude} ${it.position.longitude}")
                            .apply()

                    Log.d(TAG, "Save location ${it.position.latitude} ${it.position.longitude}")
                }

                val intent = Intent(this@MapsActivity, MainActivity::class.java)
                intent.putExtra("LOCATION", markerLocation)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                ContextCompat.startActivity(this@MapsActivity, intent, null)


            } else {
                Toast.makeText(this, "Click again to select",
                        Toast.LENGTH_SHORT).show()
                chooseCoordinats = true
                Handler().postDelayed({ chooseCoordinats = false }, 3 * 1000)
            }
            false
        }
    }

    override fun onBackPressed() {
        if (exit!!) {
            finish()
        } else {
            Toast.makeText(this, "Нажмите назад еще раз чтобы выйти",
                    Toast.LENGTH_SHORT).show()
            exit = true
            Handler().postDelayed({ exit = false }, 3 * 1000)
        }

    }
}
