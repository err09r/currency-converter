package com.app.currencyconverter.domain.usecases

import com.app.currencyconverter.constants.CommonConstants.DEFAULT_CURRENCY_CODE
import com.app.currencyconverter.domain.models.CurrencyItem
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class PerformCurrencyConversionUseCaseTest {

    private lateinit var performCurrencyConversionUseCase: PerformCurrencyConversionUseCase

    @Before
    fun setUp() {
        performCurrencyConversionUseCase = PerformCurrencyConversionUseCase()
    }

    @Test
    fun `Check if conversion result of non-default currencies is correct`() {
        val testList = listOf(
            CurrencyItem("", "XXX", 1.5),
            CurrencyItem("", "YYY", 3.0)
        )

        val expected = 1.5 / 3.0 * 2

        val actual = performCurrencyConversionUseCase(
            amountString = "2",
            currencyItems = testList,
            fromCurrencyCode = "XXX",
            toCurrencyCode = "YYY"
        )

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Check if conversion result from default currency is correct`() {
        val testList = listOf(
            CurrencyItem("", DEFAULT_CURRENCY_CODE, 1.0),
            CurrencyItem("", "USD", 4.5)
        )

        val expected = 1 / 4.5 * 2

        val actual = performCurrencyConversionUseCase(
            amountString = "2",
            currencyItems = testList,
            fromCurrencyCode = "PLN",
            toCurrencyCode = "USD"
        )

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Check if conversion result to default currency is correct`() {
        val testList = listOf(
            CurrencyItem("", DEFAULT_CURRENCY_CODE, 1.0),
            CurrencyItem("", "USD", 4.5)
        )

        val expected = 4.5 * 2

        val actual = performCurrencyConversionUseCase(
            amountString = "2",
            currencyItems = testList,
            fromCurrencyCode = "USD",
            toCurrencyCode = "PLN"
        )

        assertThat(actual).isEqualTo(expected)
    }
}