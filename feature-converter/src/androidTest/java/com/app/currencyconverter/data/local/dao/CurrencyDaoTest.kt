package com.app.currencyconverter.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.app.currencyconverter.data.local.database.CurrencyDatabase
import com.app.currencyconverter.data.local.entities.CurrencyEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class CurrencyDaoTest {

    private lateinit var database: CurrencyDatabase
    private lateinit var dao: CurrencyDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CurrencyDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.currencyDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCurrencyEntity_doesInsertCorrect() = runTest {
        val currencyEntity = CurrencyEntity("USD", "dollar", 4.5)
        dao.insertCurrency(currencyEntity)

        val currencyEntities = dao.getCurrencyList()

        assertThat(currencyEntities).contains(currencyEntity)
    }

    @Test
    fun getCurrencyList_returnsCorrectList() = runTest {
        val expectedList = listOf(
            CurrencyEntity("EUR", "euro", 4.7),
            CurrencyEntity("GBP", "pound", 5.5),
            CurrencyEntity("CZK", "korona", 0.2)
        )

        expectedList.forEach { dao.insertCurrency(it) }

        val actualList = dao.getCurrencyList()

        assertThat(actualList).containsExactlyElementsIn(expectedList)
    }
}