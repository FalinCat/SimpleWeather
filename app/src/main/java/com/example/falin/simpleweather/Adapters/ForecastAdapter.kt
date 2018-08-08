package com.example.falin.simpleweather.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.falin.simpleweather.Model.ForecastWeather.ForecastWeatherData
import com.example.falin.simpleweather.R
import com.example.falin.simpleweather.Utility.DownLoadImageTask
import com.example.falin.simpleweather.Utility.makeUrl
import kotlinx.android.synthetic.main.forecast_one_day_cardiew_layout.view.*


class ForecastAdapter(var fcwData: ForecastWeatherData?) : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.forecast_one_day_cardiew_layout, parent, false)
        return ViewHolder(v)

    }

    override fun getItemCount(): Int {
        return fcwData?.list?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dayOfWeek?.text = fcwData?.list?.get(position)?.dt_txt
        holder.temperature?.text = fcwData?.list?.get(position)?.main?.temp.toString() + "°C"
        DownLoadImageTask(holder.imageWeather).execute(makeUrl(position, fcwData!!))
    }

    fun updateData(fcw: ForecastWeatherData?) {
        fcwData = fcw
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayOfWeek = itemView.dayOfWeek
        val imageWeather = itemView.imageWeather
        val temperature = itemView.temperature
    }
}

