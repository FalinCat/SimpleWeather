package com.example.falin.simpleweather.model.ForecastWeather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wind(
        val speed: Double?,
        val deg: Double?
) : Parcelable