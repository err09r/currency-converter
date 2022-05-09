package com.app.currencyconverter.domain.usecases

import com.app.currencyconverter.constants.CommonConstants.DEFAULT_CONVERSION_VALUE
import com.app.currencyconverter.constants.CommonConstants.DEFAULT_CURRENCY_CODE
import com.app.currencyconverter.domain.models.CurrencyItem

class PerformCurrencyConversionUseCase {

    operator fun invoke(
        amountString: String,
        currencyItems: List<CurrencyItem>,
        fromCurrencyCode: String,
        toCurrencyCode: String
    ): Double {
        val amount = amountString.toDoubleOrNull() ?: DEFAULT_CONVERSION_VALUE
        val fromRate = currencyItems.getItemRate(currencyCode = fromCurrencyCode)
        val toRate = currencyItems.getItemRate(currencyCode = toCurrencyCode)

        return when (DEFAULT_CURRENCY_CODE) {
            fromCurrencyCode -> 1 / toRate * amount
            toCurrencyCode -> fromRate * amount
            else -> fromRate / toRate * amount
        }
    }

    private fun List<CurrencyItem>.getItemRate(currencyCode: String): Double {
        return this.find { it.currencyCode == currencyCode }?.rate ?: DEFAULT_CONVERSION_VALUE
    }
}