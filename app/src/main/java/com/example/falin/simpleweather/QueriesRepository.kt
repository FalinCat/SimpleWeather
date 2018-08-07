package com.example.igorvanteev.retrofit2test

import com.example.falin.simpleweather.Model.CurrentWeatherDataResponse.CurrentWeatherData
import com.example.igorvanteev.retrofit2test.Retrofit.OpenWeatherApi
import io.reactivex.Observable

class QueriesRepository(private val apiService: OpenWeatherApi) {
    fun queryCurrentWeather(latitude: Double, longitude: Double): Observable<CurrentWeatherData> {
        return apiService.search(latitude,longitude,"840c7b8b3a06bf708bfc7a3d8f4a89b3", "ru", "metric")
    }
}
