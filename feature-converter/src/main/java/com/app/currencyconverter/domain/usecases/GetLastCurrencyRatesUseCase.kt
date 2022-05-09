package com.app.currencyconverter.domain.usecases

import com.app.currencyconverter.helpers.Resource
import com.app.currencyconverter.domain.models.CurrencyItem
import com.app.currencyconverter.domain.repository.MainRepository
import javax.inject.Inject

class GetLastCurrencyRatesUseCase @Inject constructor(private val repository: MainRepository) {

    suspend operator fun invoke(): Resource<List<CurrencyItem>> {
        return repository.getLastCurrencyRates()
    }
}