package com.example.falin.simpleweather

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log


class StartActivity : AppCompatActivity() {
    private val RECORD_REQUEST_CODE = 23
    private val TAG = "APPTAG"
    private lateinit var locationManager: LocationManager
    private var isAvtivityCurrent = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        checkGpsStatus()

        checkPerm()

        checkDeviceOnline()


    }

    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            if (isAvtivityCurrent) {
                startIfWeCan()
            }
        }, 2 * 1000)

    }

    private fun startIfWeCan(): Boolean {

        if (isLocationEnabled() && isPermissionOwned() && isOnline()) {
            Log.d(TAG, "All done")
            locationUpdate()
            isAvtivityCurrent = false
            return true
        } else {
            Log.d(TAG, "not all done...")
            Log.d(TAG, "isLocationEnabled() - ${isLocationEnabled()} && " +
                    "isPermissionOwned() - ${isPermissionOwned()} &&" +
                    " isOnline() - ${isOnline()} ")
            return false
        }


    }


    // PERMISSION
    private fun checkPerm(): Boolean {
        Log.d(TAG, "checkPerm()")
        if (isPermissionOwned()) {
            return true
        } else {
            Log.d(TAG, "request permission")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), RECORD_REQUEST_CODE)
        }

        return true
    }

    private fun isPermissionOwned(): Boolean {
        return ActivityCompat.checkSelfPermission(this@StartActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this@StartActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED


    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    // Повторно спрашиваем разрешения
                    val dialog = AlertDialog.Builder(this@StartActivity)
                            .setTitle("No permission - no weather")
                            .setMessage("We can not determine your weather without knowing where you are.\n" +
                                    "Would you let the application determine your location?")
                            .setPositiveButton("YES") { dialog, which ->
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION), RECORD_REQUEST_CODE)
                            }
                            .setNegativeButton("No") { dialog, which ->
                                finish()
                            }
                            .create()

                    dialog.show()

                } else {
                    Handler().postDelayed({ startIfWeCan() }, 1 * 1000)
                }
            }
        }
    }

    // NETWORK
    private fun checkDeviceOnline(): Boolean {
        if (!isOnline()) {
            Log.d(TAG, "Device is offline")

            val dialog = AlertDialog.Builder(this@StartActivity)
                    .setTitle("Application need internet")
                    .setMessage("Turn on internet please")
                    .setPositiveButton("YES") { dialog, which ->
                        val i = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(i)
                    }
                    .setNegativeButton("No") { dialog, which ->
                        finish()
                    }
                    .create()
            dialog.show()
        }
        return isOnline()
    }

    private fun isOnline(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
//        Log.d(TAG, "isOnline - ${networkInfo != null && networkInfo.isConnected}")

        return networkInfo != null && networkInfo.isConnected
    }


    //LOCATION
    private fun checkGpsStatus(): Boolean {
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

//        Log.d(TAG, "checkGpsStatus - ${isGpsEnabled || isNetworkEnabled}")
        return isGpsEnabled || isNetworkEnabled
    }

    private fun locationUpdate() {
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Not permitted")

                return
            }

            Log.d(TAG, "request location")

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)


        } catch (e: Exception) {
            Log.e(TAG, "Error in locationUpdate\n${e.message}")
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d(TAG, "onLocationChanged()")

            val intent = Intent(this@StartActivity, MainActivity::class.java)
            intent.putExtra("LOCATION", location)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            ContextCompat.startActivity(this@StartActivity, intent, null)
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
        override fun onProviderEnabled(p0: String?) {}
        override fun onProviderDisabled(p0: String?) {}

    }

}
