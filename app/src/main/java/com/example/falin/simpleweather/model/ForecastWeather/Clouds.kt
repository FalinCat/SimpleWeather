package com.example.falin.simpleweather.model.ForecastWeather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Clouds(
        val all: Int?
) : Parcelable