package com.app.currencyconverter.domain.repository

import com.app.currencyconverter.helpers.Resource
import com.app.currencyconverter.domain.models.CurrencyItem

interface MainRepository {

    suspend fun getLastCurrencyRates(): Resource<List<CurrencyItem>>
}