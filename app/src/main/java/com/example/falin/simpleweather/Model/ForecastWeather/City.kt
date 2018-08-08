package com.example.falin.simpleweather.Model.ForecastWeather

data class City(
        val id: Int,
        val name: String,
        val coord: Coord,
        val country: String
)