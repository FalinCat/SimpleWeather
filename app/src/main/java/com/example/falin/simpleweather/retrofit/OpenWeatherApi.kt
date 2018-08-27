package com.example.igorvanteev.retrofit2test.Retrofit

import com.example.falin.simpleweather.model.CurrentWeather.CurrentWeatherData
import com.example.falin.simpleweather.model.ForecastWeather.ForecastWeatherData
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface OpenWeatherApi {
    @GET("data/2.5/weather")
    fun queryCurrentWeather(@Query("lat") lat: Double,
                            @Query("lon") lon: Double,
                            @Query("appid") appid: String,
                            @Query("lang") lang: String,
                            @Query("units") units: String
    ): io.reactivex.Observable<CurrentWeatherData>

    @GET("data/2.5/forecast")
    fun queryForecast(@Query("lat") lat: Double,
                      @Query("lon") lon: Double,
                      @Query("appid") appid: String,
                      @Query("lang") lang: String,
                      @Query("units") units: String
    ): io.reactivex.Observable<ForecastWeatherData>

    companion object Factory {
        fun create(): OpenWeatherApi {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://api.openweathermap.org")
                    .build()

            return retrofit.create(OpenWeatherApi::class.java)
        }
    }
}