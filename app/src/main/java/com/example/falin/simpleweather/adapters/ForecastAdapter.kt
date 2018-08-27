package com.example.falin.simpleweather.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.falin.simpleweather.Model.ForecastWeather.ForecastWeatherData
import com.example.falin.simpleweather.R
import kotlinx.android.synthetic.main.forecast_one_day_cardiew_layout.view.*
import java.text.SimpleDateFormat
import java.util.*


class ForecastAdapter(var fcwData: ForecastWeatherData?) : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.forecast_one_day_cardiew_layout, parent, false)
        return ViewHolder(v)

    }

    override fun getItemCount(): Int = fcwData?.list?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        val netDate = Date(fcwData!!.list?.get(position)?.dt!!.toLong() * 1000)

        val temperature = "${String.format("%.1f", fcwData!!.list?.get(position)?.main?.temp).replace(",", ".")} °C"
        val wind = "${String.format("%.1f", fcwData!!.list?.get(position)?.wind?.speed).replace(",", ".")} m/s"
        val pressure = "${String.format("%.1f", fcwData!!.list?.get(position)?.main?.pressure).replace(",", ".")} hPa"

        holder.dayOfWeek?.text = sdf.format(netDate)
        holder.temperature?.text = temperature
        holder.wind?.text = wind
        holder.pressure?.text = pressure


//        DownLoadImageTask(holder.imageWeather).execute(makeUrl(position, fcwData!!))
    }

    fun updateData(fcw: ForecastWeatherData?) {
        fcwData = fcw
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayOfWeek = itemView.dayOfWeekFC
        val temperature = itemView.temperatureFC
        val wind = itemView.windFC
        val pressure = itemView.pressureFC
    }
}

