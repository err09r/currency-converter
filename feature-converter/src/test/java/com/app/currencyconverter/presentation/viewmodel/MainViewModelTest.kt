package com.app.currencyconverter.presentation.viewmodel

import app.cash.turbine.test
import com.app.currencyconverter.TestDispatchers
import com.app.currencyconverter.domain.usecases.GetLastCurrencyRatesUseCase
import com.app.currencyconverter.domain.usecases.PerformCurrencyConversionUseCase
import com.app.currencyconverter.helpers.ErrorType
import com.app.currencyconverter.helpers.Mode
import com.app.currencyconverter.helpers.Resource
import com.app.currencyconverter.helpers.UiState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock


@ExperimentalCoroutinesApi
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    private val getLastCurrencyRatesUseCase = mock<GetLastCurrencyRatesUseCase>()
    private val performCurrencyConversionUseCase = mock<PerformCurrencyConversionUseCase>()

    @Before
    fun setUp() {
        viewModel = MainViewModel(
            dispatchers = TestDispatchers,
            getLastCurrencyRatesUseCase = getLastCurrencyRatesUseCase,
            performCurrencyConversionUseCase = performCurrencyConversionUseCase
        )
    }

    @After
    fun tearDown() {
        Mockito.reset(getLastCurrencyRatesUseCase, performCurrencyConversionUseCase)
    }

    @Test
    fun `Check if uiState is correctly updated after successful data request`() = runTest {
        Mockito.`when`(getLastCurrencyRatesUseCase())
            .thenReturn(Resource.Success(emptyList(), Mode.ONLINE))

        viewModel.uiState.test {
            viewModel.loadData()
            skipItems(1)
            val actual = awaitItem()
            assertThat(actual is UiState.Success).isTrue()
        }

        Mockito.verify(getLastCurrencyRatesUseCase).invoke()
    }

    @Test
    fun `Check if uiState is correctly updated after failed data request`() = runTest {
        Mockito.`when`(getLastCurrencyRatesUseCase()).thenReturn(Resource.Failure(ErrorType.UNKNOWN))

        viewModel.uiState.test {
            viewModel.loadData()
            skipItems(1)
            val actual = awaitItem()
            assertThat(actual is UiState.Error).isTrue()
        }

        Mockito.verify(getLastCurrencyRatesUseCase).invoke()
    }

    @Test
    fun `Check if conversionResult is correctly updated after conversion`() = runTest {
        Mockito.`when`(
            performCurrencyConversionUseCase(
                amountString = "",
                currencyItems = emptyList(),
                fromCurrencyCode = "",
                toCurrencyCode = ""
            )
        ).thenReturn(1.0)

        viewModel.conversionResult.test {
            val defaultConversionValue = awaitItem()
            viewModel.convert("", "", "")
            val actualConversionValue = awaitItem()
            assertThat(actualConversionValue).isNotEqualTo(defaultConversionValue)
        }

        Mockito
            .verify(performCurrencyConversionUseCase)
            .invoke("", emptyList(), "", "")
    }
}