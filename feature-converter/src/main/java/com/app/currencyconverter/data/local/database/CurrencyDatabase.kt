package com.app.currencyconverter.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.currencyconverter.data.local.dao.CurrencyDao
import com.app.currencyconverter.data.local.entities.CurrencyEntity

private const val DATABASE_VERSION = 1

@Database(
    entities = [CurrencyEntity::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}
