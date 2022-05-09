package com.app.currencyconverter.domain.models

data class CurrencyItem(
    val currencyTitle: String,
    val currencyCode: String,
    val rate: Double
)