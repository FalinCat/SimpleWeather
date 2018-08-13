package com.example.falin.simpleweather.Utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.falin.simpleweather.Model.CurrentWeather.CurrentWeatherData
import com.example.falin.simpleweather.Model.ForecastWeather.ForecastWeatherData
import java.net.URL

class DownLoadImageTask(internal val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
    override fun doInBackground(vararg urls: String): Bitmap? {
        val urlOfImage = urls[0]
        return try {
            val inputStream = URL(urlOfImage).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: Bitmap?) {
        if (result != null) {
            imageView.setImageBitmap(result)
        } else {
            Toast.makeText(imageView.context, "Error downloading", Toast.LENGTH_SHORT).show()
        }


    }
}

fun makeUrl(position: Int, fcwData: ForecastWeatherData): String {
    val imageName = fcwData.list[position].weather[0].icon
    Log.d("APPTAG", "Forecast icon " + fcwData.list[position].weather[0].icon + "\nPosition $position")

    return "http://openweathermap.org/img/w/$imageName.png"
}

fun makeUrl(curw: CurrentWeatherData): String {
    val imageName = curw.weather[0].icon
    Log.d("APPTAG", "Current icon " + curw.weather[0].icon)

    return "http://openweathermap.org/img/w/$imageName.png"
}

fun makeUrl(icon: String): String {
    return "http://openweathermap.org/img/w/$icon.png"
}