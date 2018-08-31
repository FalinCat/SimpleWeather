package com.example.igorvanteev.retrofit2test

import com.example.falin.simpleweather.model.CurrentWeather.CurrentWeatherData
import com.example.falin.simpleweather.model.ForecastWeather.ForecastWeatherData
import com.example.igorvanteev.retrofit2test.Retrofit.IOpenWeatherApi
import io.reactivex.Observable

class QueriesRepository(private val apiServiceI: IOpenWeatherApi) {
    fun queryCurrentWeather(latitude: Double, longitude: Double): Observable<CurrentWeatherData> {
        return apiServiceI.queryCurrentWeather(latitude, longitude, "840c7b8b3a06bf708bfc7a3d8f4a89b3", "ru", "metric")
    }

    fun queryForecastWeather(latitude: Double, longitude: Double): Observable<ForecastWeatherData> {
        return apiServiceI.queryForecast(latitude, longitude, "840c7b8b3a06bf708bfc7a3d8f4a89b3", "ru", "metric")
    }
}
