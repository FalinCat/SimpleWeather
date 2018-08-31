package com.example.falin.simpleweather.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.falin.simpleweather.LOG_TAG
import com.example.falin.simpleweather.R
import com.example.falin.simpleweather.model.CurrentWeather.CurrentWeatherData
import com.example.falin.simpleweather.model.ForecastWeather.ForecastWeatherData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_body.view.*
import kotlinx.android.synthetic.main.recycler_header.view.*
import java.text.SimpleDateFormat
import java.util.*


class ForecastAdapter(val context: Context, var cWeather: CurrentWeatherData, var fWeather: ForecastWeatherData) : RecyclerView.Adapter<ForecastAdapter.Companion.RecyclerViewHolder>() {
    companion object {
        val TYPE_HEAD = 0
        val TYPE_BODY = 1

        class RecyclerViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
            var view_type = 0
            // Текущая погода
            lateinit var windSpeed: TextView
            lateinit var humidity: TextView
            lateinit var pressureCurrent: TextView
            lateinit var temperatureCurrent: TextView
            lateinit var weatherDescription: TextView
            lateinit var currentWeatherImage: ImageView
            // Прогноз погоды
            lateinit var dayOfWeek: TextView
            lateinit var temperature: TextView
            lateinit var wind: TextView
            lateinit var pressure: TextView
            lateinit var weatherImage: ImageView

            init {
                if (viewType == TYPE_BODY) {
                    dayOfWeek = itemView.dayOfWeekFC
                    temperature = itemView.temperatureFC
                    wind = itemView.windFC
                    pressure = itemView.pressureFC
                    weatherImage = itemView.weatherImageFC
                    view_type = 1
                } else if (viewType == TYPE_HEAD) {
                    windSpeed = itemView.WindSpeedValue
                    humidity = itemView.HumidityValue
                    pressureCurrent = itemView.PressureValue
                    temperatureCurrent = itemView.TemperatureValue
                    currentWeatherImage = itemView.WeatherImage
                    weatherDescription = itemView.WeatherDescriptionValue
                    view_type = 0
                }
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        if (holder.view_type == TYPE_BODY) {
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            val netDate = Date(fWeather.list!![position].dt!!.toLong() * 1000)
            val temperature = "${String.format("%.1f", fWeather.list!![position].main?.temp).replace(",", ".")} °C"
            val wind = "${String.format("%.1f", fWeather.list!![position].wind?.speed).replace(",", ".")} м/с"
            val pressure = "${String.format("%.1f", fWeather.list!![position].main?.pressure!! / 10).replace(",", ".")} кПа"

            val imageUrl = "http://openweathermap.org/img/w/${fWeather.list!![position].weather!![0].icon}.png"

            Picasso.get().load(imageUrl).into(holder.weatherImage)

            holder.dayOfWeek.text = sdf.format(netDate)
            holder.temperature.text = temperature
            holder.wind.text = wind
            holder.pressure.text = pressure
        } else if (holder.view_type == TYPE_HEAD) {
            val imageUrl = "http://openweathermap.org/img/w/${cWeather.weather[0].icon}.png"
            val hum = "${cWeather.main.humidity} %"
            val wind = "${cWeather.wind.speed} м/с"
            val pressure = "${cWeather.main.pressure / 10} кПа"

            holder.temperatureCurrent.text = String.format("%.1f", cWeather.main.temp).replace(",", ".").plus(" °C")
            holder.humidity.text = hum
            holder.windSpeed.text = wind
            holder.pressureCurrent.text = pressure
            holder.weatherDescription.text = cWeather.weather[0].description.capitalize()

            Picasso.get().load(imageUrl).into(holder.currentWeatherImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val rv: View
        val holder: RecyclerViewHolder

        if (viewType == TYPE_HEAD) {
            rv = LayoutInflater.from(parent.context).inflate(R.layout.recycler_header, parent, false)
            holder = RecyclerViewHolder(rv, viewType)
            return holder
        }
        rv = LayoutInflater.from(parent.context).inflate(R.layout.recycler_body, parent, false)
        holder = RecyclerViewHolder(rv, viewType)
        return holder
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> TYPE_HEAD
        else -> TYPE_BODY
    }

    override fun getItemCount(): Int {
        return fWeather.list?.size!!
    }

    fun updateData(cwd: CurrentWeatherData, fcw: ForecastWeatherData) {
        Log.d(LOG_TAG, "Обновление данных в адаптере")
        cWeather = cwd
        fWeather = fcw
        notifyDataSetChanged()
    }
}
