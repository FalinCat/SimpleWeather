package com.example.falin.simpleweather.Model.ForecastWeather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Coord(
        val lat: Double?,
        val lon: Double?
) : Parcelable