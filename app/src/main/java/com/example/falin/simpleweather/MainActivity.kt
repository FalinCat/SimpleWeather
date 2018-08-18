@file:Suppress("DEPRECATION")

package com.example.falin.simpleweather

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.example.falin.simpleweather.Adapters.ForecastAdapter
import com.example.falin.simpleweather.Model.CurrentWeather.CurrentWeatherData
import com.example.falin.simpleweather.Model.ForecastWeather.ForecastWeatherData
import com.example.falin.simpleweather.Utility.DownLoadImageTask
import com.example.falin.simpleweather.Utility.makeUrl
import com.example.igorvanteev.retrofit2test.QueryRepositoryProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private val TAG = "APPTAG"
    private var exit: Boolean? = false
    private lateinit var location: Location
    private val repo = QueryRepositoryProvider.provideQueryRepository()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        forecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        location = intent.getParcelableExtra("LOCATION")
    }


    private fun addSubscription() {
        compositeDisposable.add(
                repo.queryCurrentWeather(location.latitude, location.longitude)
                        .repeatWhen { repeatHandler ->
                            repeatHandler.flatMap { Observable.timer(60, TimeUnit.SECONDS) }
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { result ->
                                    Log.d(TAG, result.toString())
                                    updateUI(result)
                                },
                                { error -> Log.e(TAG, error.message) }
                        )
        )


        compositeDisposable.add(
                repo.queryForecastWeather(location.latitude, location.longitude)

                        .repeatWhen { repeatHandler ->
                            repeatHandler.flatMap { Observable.timer(1800, TimeUnit.SECONDS) }
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            updateUI(result)
                        }, { error ->
                            Log.e(TAG, "Error in forecast - ${error.message}")
                        })
        )
    }

    private fun updateUI(cWeather: CurrentWeatherData) {
        val hum = "Humidity -  ${cWeather.main.humidity} %"
        val wind = "Wind Speed -  ${cWeather.wind.speed} m/s"
        val minMax = """Min ${String.format("%.1f", cWeather.main.temp_min).replace(",", ".")} °C - Max ${String.format("%.1f", cWeather.main.temp_max).replace(",", ".")} °C"""
        currentTemperatureTxt.text = String.format("%.1f", cWeather.main.temp).replace(",", ".").plus(" °C")
        locationText.text = cWeather.name

        currentHumidity.text = hum
        currentWindSpeed.text = wind
        minMaxTemp.text = minMax

        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val lastUpdt = "Last update: ${format.format(Date())}"
        lastUpdate.text = lastUpdt

        DownLoadImageTask(currentWeatherImage).execute(makeUrl(cWeather.weather[0].icon))

        Log.d(TAG, "UI updating...")
    }

    private fun updateUI(fWeather: ForecastWeatherData) {
        try {
            if (forecastRecyclerView.adapter != null) {
                val adapter = ForecastAdapter(fWeather)
                adapter.updateData(fWeather)
            } else {
            }
            forecastRecyclerView.adapter = ForecastAdapter(fWeather)
        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }

    }

    override fun onResume() {
        super.onResume()
        addSubscription()
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
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
