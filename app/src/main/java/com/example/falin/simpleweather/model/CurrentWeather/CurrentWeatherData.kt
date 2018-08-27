package com.example.falin.simpleweather.model.CurrentWeather

data class CurrentWeatherData(
        val coord: Coord,
        val weather: List<Weather>,
        val base: String,
        val main: Main,
        val wind: Wind,
        val clouds: Clouds,
        val dt: Int,
        val sys: Sys,
        val id: Int,
        val name: String,
        val cod: Int
)