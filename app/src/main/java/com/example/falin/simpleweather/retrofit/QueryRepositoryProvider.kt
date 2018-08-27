package com.example.igorvanteev.retrofit2test

import com.example.igorvanteev.retrofit2test.Retrofit.OpenWeatherApi

object QueryRepositoryProvider {

    fun provideQueryRepository(): QueriesRepository {
        return QueriesRepository(OpenWeatherApi.create())
    }

}