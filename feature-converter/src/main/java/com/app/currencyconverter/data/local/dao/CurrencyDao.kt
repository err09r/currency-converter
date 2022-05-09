package com.app.currencyconverter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.app.currencyconverter.data.local.entities.CurrencyEntity

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currencyentity")
    fun getCurrencyList(): List<CurrencyEntity>

    @Insert(onConflict = REPLACE)
    fun insertCurrency(entity: CurrencyEntity)
}