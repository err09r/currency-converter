package com.app.currencyconverter.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CurrencyDto(
    @SerializedName("currency")
    val currencyTitle: String,
    @SerializedName("code")
    val currencyCode: String,
    @SerializedName("mid")
    val rate: Double
)