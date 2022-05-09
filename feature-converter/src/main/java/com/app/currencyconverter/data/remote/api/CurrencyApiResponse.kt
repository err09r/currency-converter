package com.app.currencyconverter.data.remote.api

import com.app.currencyconverter.data.remote.dto.CurrencyDto
import com.google.gson.annotations.SerializedName

data class CurrencyApiResponse(
    @SerializedName("table")
    val table: String,
    @SerializedName("no")
    val number: String,
    @SerializedName("effectiveDate")
    val effectiveDate: String,
    @SerializedName("rates")
    val currencyRates: List<CurrencyDto>
)