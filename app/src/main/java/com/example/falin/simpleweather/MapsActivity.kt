package com.example.falin.simpleweather

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var chooseCoordinats = false
    private val TAG = "APPTAG"
    private var exit: Boolean? = false
    private var APP_PREFERENCE = "appPrefs"
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        prefs = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        var location: LatLng

        mMap.setOnMapClickListener {

            mMap.clear()

            location = LatLng(it.latitude, it.longitude)
            mMap.addMarker(MarkerOptions().position(location).title("Your location"))

//            Toast.makeText(this,
//                    "Latitude=${it.latitude} \nLongitude=${it.longitude}",
//                    Toast.LENGTH_SHORT).show()
        }

        mMap.setOnMarkerClickListener {

            if (chooseCoordinats) {
                val markerLocation = Location("")
                markerLocation.latitude = it.position.latitude
                markerLocation.longitude = it.position.longitude

                prefs.edit()
                        .putString("LOCATION", "${it.position.latitude} ${it.position.longitude}")
                        .apply()

                Log.i(TAG, "Save location ${it.position.latitude} ${it.position.longitude}")

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
                Handler().postDelayed({ chooseCoordinats = false }, 5 * 1000)
            }



            false
        }
    }

    override fun onBackPressed() {
        if (exit!!) {
            finish()
        } else {
            Toast.makeText(this, "Press Back again to Exit",
                    Toast.LENGTH_SHORT).show()
            exit = true
            Handler().postDelayed({ exit = false }, 3 * 1000)
        }

    }
}
