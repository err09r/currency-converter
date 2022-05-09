package com.app.currencyconverter.data.remote.api

import retrofit2.Response
import retrofit2.http.GET


interface CurrencyApi {

    @GET("api/exchangerates/tables/a/last")
    suspend fun loadCurrencyList(): Response<List<CurrencyApiResponse>>
}