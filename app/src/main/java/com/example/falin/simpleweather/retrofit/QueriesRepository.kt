package com.example.igorvanteev.retrofit2test

import com.example.falin.simpleweather.model.CurrentWeather.CurrentWeatherData
import com.example.falin.simpleweather.model.ForecastWeather.ForecastWeatherData
import com.example.igorvanteev.retrofit2test.Retrofit.OpenWeatherApi
import io.reactivex.Observable

class QueriesRepository(private val apiService: OpenWeatherApi) {
    fun queryCurrentWeather(latitude: Double, longitude: Double): Observable<CurrentWeatherData> {
        return apiService.queryCurrentWeather(latitude, longitude, "840c7b8b3a06bf708bfc7a3d8f4a89b3", "ru", "metric")
    }

    fun queryForecastWeather(latitude: Double, longitude: Double): Observable<ForecastWeatherData> {
        return apiService.queryForecast(latitude, longitude, "840c7b8b3a06bf708bfc7a3d8f4a89b3", "ru", "metric")
    }
}
