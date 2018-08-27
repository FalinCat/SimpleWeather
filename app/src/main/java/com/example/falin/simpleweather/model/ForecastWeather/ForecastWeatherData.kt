package com.example.falin.simpleweather.model.ForecastWeather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ForecastWeatherData(
        val cod: String?,
        val message: Double?,
        val cnt: Int?,
        val list: List<X>?,
        val city: City?
) : Parcelable