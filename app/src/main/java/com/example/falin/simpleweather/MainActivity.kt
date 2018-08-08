@file:Suppress("DEPRECATION")

package com.example.falin.simpleweather

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import com.example.falin.simpleweather.Adapters.ForecastAdapter
import com.example.falin.simpleweather.Controller.LocationController
import com.example.falin.simpleweather.Utility.DownLoadImageTask
import com.example.falin.simpleweather.Utility.makeUrl
import com.example.igorvanteev.retrofit2test.QueryRepositoryProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val TAG = "APPTAG"

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val location = LocationController(this)

        forecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)


        //Init API
        val repo = QueryRepositoryProvider.provideQueryRepository()

        compositeDisposable.add(
                repo.queryCurrentWeather(location.latitude, location.longitude)
                        .observeOn(AndroidSchedulers.mainThread())
                        .repeatWhen { repeatHandler ->
                            repeatHandler.flatMap { Observable.timer(60, TimeUnit.SECONDS) }
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            currentTemperatureTxt.text = "${result.main.temp} °C"
                            locationText.text = result.name
                            DownLoadImageTask(currentWeatherImage).execute(makeUrl(result))
                        }, { error ->
                            Log.i(TAG, "Error in current weather - ${error.message}")
                        })
        )

        compositeDisposable.add(
                repo.queryForecastWeather(location.latitude, location.longitude)
                        .observeOn(AndroidSchedulers.mainThread())
                        .repeatWhen { repeatHandler ->
                            repeatHandler.flatMap { Observable.timer(60, TimeUnit.SECONDS) }
                        }
                        .subscribeOn(Schedulers.io())
                        .doOnComplete { }
                        .subscribe({ result ->
                            if (forecastRecyclerView.adapter != null) {
                                val adapter = ForecastAdapter(result)
                                adapter.updateData(result)
                            } else
                                forecastRecyclerView.adapter = ForecastAdapter(result)
                        }, { error ->
                            Log.i(TAG, "Error in forecast - ${error.message}")
                        })

        )


    }


    override fun onStart() {
        super.onStart()
        checkLocation()
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
