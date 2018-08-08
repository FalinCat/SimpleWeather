package com.example.falin.simpleweather.Model.ForecastWeather

data class X(
        val dt: Int,
        val main: Main,
        val weather: List<Weather>,
        val clouds: Clouds,
        val wind: Wind,
        val rain: Rain,
        val sys: Sys,
        val dt_txt: String
)