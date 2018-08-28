@file:Suppress("DEPRECATION")

package com.example.falin.simpleweather

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.example.falin.simpleweather.adapters.ForecastAdapter
import com.example.falin.simpleweather.model.CurrentWeather.CurrentWeatherData
import com.example.falin.simpleweather.model.ForecastWeather.ForecastWeatherData
import com.example.igorvanteev.retrofit2test.QueryRepositoryProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private val TAG = "APPTAG"
    private var exit: Boolean? = false
    private lateinit var location: Location
    private val repo = QueryRepositoryProvider.provideQueryRepository()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var APP_PREFERENCE = "appPrefs"
    private lateinit var prefs: SharedPreferences
    private lateinit var fcw: ForecastWeatherData
    private lateinit var cwd: CurrentWeatherData
    private var isCUrWeatherReady: Boolean = false
    private var isForecastReady: Boolean = false
    private lateinit var mDrawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_root_layout)

        prefs = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
        forecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        location = intent.getParcelableExtra("LOCATION")

        mDrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            TODO("Нет времени на доработку :(")
            true
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


    private fun addSubscription() {
        Log.d(LOG_TAG, "Добавляем подписки")
        compositeDisposable.addAll(
                repo.queryCurrentWeather(location.latitude, location.longitude)
                        .repeatWhen { repeatHandler ->
                            repeatHandler.flatMap { Observable.timer(1, TimeUnit.HOURS) }
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { result ->
                                    cwd = result
                                    isCUrWeatherReady = true
                                    updateUI("current")
                                },
                                { error -> Log.e(TAG, error.message) }
                        ),

                repo.queryForecastWeather(location.latitude, location.longitude)
                        .repeatWhen { repeatHandler ->
                            repeatHandler.flatMap { Observable.timer(6, TimeUnit.HOURS) }
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            fcw = result
                            isForecastReady = true
                            updateUI("forecast")
                        }, { error ->
                            Log.e(TAG, "Error in forecast - ${error.message}")
                        })
        )
    }

    private fun updateUI(from: String) {
        Log.d(LOG_TAG, "Попытка обновление интерфейса из $from")
        if (isCUrWeatherReady && isForecastReady) {
            Log.d(LOG_TAG, "Обновление интерфейса")
            try {
                if (forecastRecyclerView.adapter != null) {
                    val adapter = ForecastAdapter(this, cwd, fcw)
                    adapter.updateData(cwd, fcw)
                } else {
                    forecastRecyclerView.adapter = ForecastAdapter(this, cwd, fcw)
                }

                isCUrWeatherReady = false
                isForecastReady = false

            } catch (e: Exception) {

            }
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
