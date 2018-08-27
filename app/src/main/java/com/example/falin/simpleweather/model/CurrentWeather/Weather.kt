package com.example.falin.simpleweather.model.CurrentWeather

data class Weather(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
)