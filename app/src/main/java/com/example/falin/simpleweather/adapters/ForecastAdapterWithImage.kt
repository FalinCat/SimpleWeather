package com.example.falin.simpleweather.adapters

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.falin.simpleweather.DetailForecastActivity
import com.example.falin.simpleweather.R
import com.example.falin.simpleweather.model.ForecastWeather.ForecastWeatherData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_body.view.*
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapterWithImage(var fcwData: ForecastWeatherData?) : RecyclerView.Adapter<ForecastAdapterWithImage.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_body, parent, false)

        return ViewHolder(view).listen { pos, type ->
            val intent = Intent(parent.context, DetailForecastActivity::class.java)
            intent.putExtra("fcw", fcwData)
            intent.putExtra("position", pos)

            parent.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = fcwData?.list?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        val netDate = Date(fcwData!!.list!![position].dt!!.toLong() * 1000)

        val temperature = "${String.format("%.1f", fcwData!!.list!![position].main?.temp).replace(",", ".")} °C"
        val wind = "${String.format("%.1f", fcwData!!.list!![position].wind?.speed).replace(",", ".")} m/s"
        val pressure = "${String.format("%.1f", fcwData!!.list!![position].main?.pressure).replace(",", ".")} hPa"

        val imageUrl = "http://openweathermap.org/img/w/${fcwData!!.list!![position].weather!![0].icon}.png"

        Picasso.get().load(imageUrl).into(holder.weatherImage)

        holder.dayOfWeek.text = sdf.format(netDate)
        holder.temperature.text = temperature
        holder.wind.text = wind
        holder.pressure.text = pressure
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayOfWeek: TextView = itemView.dayOfWeekFC
        val temperature: TextView = itemView.temperatureFC
        val wind: TextView = itemView.windFC
        val pressure: TextView = itemView.pressureFC

        val weatherImage: ImageView = itemView.weatherImageFC
    }

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }

    fun updateData(fcw: ForecastWeatherData?) {
        fcwData = fcw
        notifyDataSetChanged()
    }
}