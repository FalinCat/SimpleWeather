package com.example.falin.simpleweather.Model.CurrentWeatherDataResponse

data class Weather(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
)