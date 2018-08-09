@file:Suppress("DEPRECATION")

package com.example.falin.simpleweather

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import com.example.falin.simpleweather.Adapters.ForecastAdapter
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



        forecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)

        val location = intent.getParcelableExtra<Location>("EXTRA")

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
}
