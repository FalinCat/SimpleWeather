package com.example.igorvanteev.retrofit2test

import com.example.igorvanteev.retrofit2test.Retrofit.IOpenWeatherApi

object QueryRepositoryProvider {

    fun provideQueryRepository(): QueriesRepository {
        return QueriesRepository(IOpenWeatherApi.create())
    }

}