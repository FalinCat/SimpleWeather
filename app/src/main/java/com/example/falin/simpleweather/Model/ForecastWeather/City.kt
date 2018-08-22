package com.example.falin.simpleweather.Model.ForecastWeather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
        val id: Int?,
        val name: String?,
        val coord: Coord?,
        val country: String?
) : Parcelable