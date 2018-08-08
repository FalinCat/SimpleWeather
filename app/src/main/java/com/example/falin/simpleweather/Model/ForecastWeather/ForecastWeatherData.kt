package com.example.falin.simpleweather.Model.ForecastWeather

data class ForecastWeatherData(
        val cod: String,
        val message: Double,
        val cnt: Int,
        val list: List<X>,
        val city: City
)