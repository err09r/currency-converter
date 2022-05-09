package com.app.currencyconverter.domain.usecases

import com.app.currencyconverter.domain.models.CurrencyItem
import com.app.currencyconverter.domain.repository.MainRepository
import com.app.currencyconverter.helpers.Mode
import com.app.currencyconverter.helpers.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class GetLastCurrencyRatesUseCaseTest {

    private lateinit var getLastCurrencyRatesUseCase: GetLastCurrencyRatesUseCase

    private val repository = mock<MainRepository>()

    @Before
    fun setUp() {
        getLastCurrencyRatesUseCase = GetLastCurrencyRatesUseCase(repository)
    }

    @After
    fun tearDown() {
        Mockito.reset(repository)
    }

    @Test
    fun `Check if repository returns correct data`() = runTest {
        val testList = listOf(
            CurrencyItem("", "XXX", 1.0),
            CurrencyItem("", "YYY", 2.0)
        )

        val expectedResource = Resource.Success(content = testList, mode = Mode.ONLINE)
        Mockito.`when`(repository.getLastCurrencyRates()).thenReturn(expectedResource)

        val actualResource = getLastCurrencyRatesUseCase()

        assertThat(actualResource).isEqualTo(expectedResource)
        Mockito.verify(repository).getLastCurrencyRates()
    }
}