package com.example.falin.simpleweather

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log

const val LOG_TAG = "APPTAG"
const val FRAGMENT_SETTINGS = "Settings Fragment"

class StartActivity : AppCompatActivity() {
    private var locationManager: LocationManager? = null
    private var userLocation: Location? = null
    private val eventHandler: Handler = Handler()
    private val RECORD_REQUEST_CODE = 23
    private val TAG = "APPTAG"
    private var APP_PREFERENCE = "appPrefs"
    private lateinit var prefs: SharedPreferences
    private lateinit var userPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        prefs = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
        userPrefs = PreferenceManager.getDefaultSharedPreferences(this)

    }

    override fun onStart() {
        super.onStart()

        if (prefs.getString("LOCATION", "") != "" && userPrefs.getBoolean("useSavedLocation", true)) {
            val stringLocations = prefs.getString("LOCATION", "").split(" ")
            Log.i(TAG, "Найдены сохраненные координаты ${stringLocations[0]} & ${stringLocations[1]}")
            userLocation = Location("")
            userLocation?.latitude = stringLocations[0].toDouble()
            userLocation?.longitude = stringLocations[1].toDouble()

            eventHandler.postDelayed({
                eventHandler.removeCallbacksAndMessages(null)
                startMainActivity()
            }, 2 * 1000)

        } else {
            // Проверка разрешений
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(LOG_TAG, "Нет разрешений. Запрашиваем")

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), RECORD_REQUEST_CODE)

                return
            }

            //Проверяем включено ли местоположение
            if (!isLocationEnabled()) {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Использовать местоположение")
                        .setMessage("Включить геолокацию")
                        .setPositiveButton("Настройки местоположения") { _, _ ->
                            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(myIntent)
                        }
                        .setNegativeButton("Нет") { _, _ -> startMapsActivity() }
                dialog.show()
            }

            // Проверяем включен ли интернет
            if (!checkDeviceOnline()) {
                Log.d(TAG, "На устройстве выключен интернет")

                val dialog = AlertDialog.Builder(this@StartActivity)
                        .setTitle("Доступ к сети")
                        .setMessage("Приложению требуется доступ к интернету\nВключить интернет?")
                        .setPositiveButton("Да") { _, _ ->
                            val i = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(i)
                        }
                        .setNegativeButton("Нет") { _, _ ->
                            finish()
                        }
                        .create()
                dialog.show()
            }

            // Единичный запрос координат
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
            locationManager?.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
            locationManager?.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)

            eventHandler.postDelayed({
                if (userLocation != null) {
                    eventHandler.removeCallbacksAndMessages(null)
                    locationManager?.removeUpdates(locationListener)
                    startMainActivity()
                } else {
                    Log.d(LOG_TAG, "Координаты не определены спустя 2 секунды")
                }

            }, 2 * 1000)

            eventHandler.postDelayed({
                if (userLocation != null) {
                    eventHandler.removeCallbacksAndMessages(null)
                    locationManager?.removeUpdates(locationListener)
                    startMainActivity()
                } else {
                    Log.d(LOG_TAG, "Координаты не определены. Переход к карте")
                    startMapsActivity()
                }
            }, 10 * 1000)
        }
    }

    private fun startMapsActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.putExtra("fromStartActivity", true)
        startActivity(intent)
    }

    private fun startMainActivity() {
        Log.d(LOG_TAG, "Координаты для поиска погоды ${userLocation?.latitude} && ${userLocation?.longitude}")

        //Записываем местоположение в настройки
        if (prefs.getString("LOCATION", "") == "" && userPrefs.getBoolean("useSavedLocation", true)) {
            prefs.edit()
                    .putString("LOCATION", "${userLocation?.latitude} ${userLocation?.longitude}")
                    .apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("LOCATION", userLocation)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    // Повторно спрашиваем разрешения
                    val dialog = AlertDialog.Builder(this@StartActivity)
                            .setTitle("Разрешение определения местоположения")
                            .setMessage("Определить местоположение автоматически?")
                            .setPositiveButton("Да") { _, _ ->
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION), RECORD_REQUEST_CODE)
                            }
                            .setNegativeButton("Нет") { _, _ ->
                                startMapsActivity()
                            }
                            .create()

                    dialog.show()

                } else {
                    Log.d(LOG_TAG, "Разрешение получено")
                    recreate()
                }
            }
        }
    }

    // NETWORK
    private fun checkDeviceOnline(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo

        val isOnline = networkInfo != null && networkInfo.isConnected
        if (!isOnline) {
            Log.d(TAG, "На устройстве выключен интернет")

            val dialog = AlertDialog.Builder(this@StartActivity)
                    .setTitle("Доступ к сети")
                    .setMessage("Приложению требуется доступ к интернету\nВключить интернет?")
                    .setPositiveButton("Да") { dialog, which ->
                        val i = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(i)
                    }
                    .setNegativeButton("Нет") { dialog, which ->
                        finish()
                    }
                    .create()
            dialog.show()
        }
        return isOnline
    }

    //LOCATION
    private fun isLocationEnabled(): Boolean {
        val isGpsEnabled = (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.NETWORK_PROVIDER)

//        Log.d(TAG, "checkGpsStatus - ${isGpsEnabled || isNetworkEnabled}")
        return isGpsEnabled || isNetworkEnabled
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            userLocation = location
            locationManager?.removeUpdates(this)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

}
