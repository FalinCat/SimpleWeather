package com.example.falin.simpleweather

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.falin.simpleweather.model.ForecastWeather.ForecastWeatherData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_forecast.*
import java.text.SimpleDateFormat
import java.util.*

class DetailForecastActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_forecast)

        val fcw = intent.getParcelableExtra<ForecastWeatherData>("fcw")
        val position = intent.getIntExtra("position", 0)

        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        val netDate = Date(fcw!!.list!![position].dt!!.toLong() * 1000)
        val imageUrl = "http://openweathermap.org/img/w/${fcw.list!![position].weather!![0].icon}.png"


        Picasso.get().load(imageUrl).into(WeatherImage)

        LocationTextView.text = fcw.city?.name
        DateTextView.text = sdf.format(netDate)

        TemperatureValue.text = fcw.list[position].main?.temp.toString() + " °C"
        PressureValue.text = fcw.list[position].main?.grnd_level.toString() + " hPa"
        HumidityValue.text = fcw.list[position].main?.humidity.toString() + " %"
        WindSpeedValue.text = fcw.list[position].wind?.speed.toString() + " m/s"
        WeatherConditionValue.text = fcw.list[position].weather!![0].description
        CloudsValue.text = fcw.list[position].clouds!!.all.toString() + " %"


    }
}
