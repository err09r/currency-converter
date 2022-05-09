package com.app.currencyconverter.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyEntity(
    @PrimaryKey val currencyCode: String,
    val currencyTitle: String,
    val rate: Double
)