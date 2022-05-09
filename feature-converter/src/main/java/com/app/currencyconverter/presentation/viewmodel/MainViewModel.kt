package com.app.currencyconverter.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currencyconverter.constants.CommonConstants.DEFAULT_CONVERSION_VALUE
import com.app.currencyconverter.domain.models.CurrencyItem
import com.app.currencyconverter.domain.usecases.GetLastCurrencyRatesUseCase
import com.app.currencyconverter.domain.usecases.PerformCurrencyConversionUseCase
import com.app.currencyconverter.helpers.DispatcherProvider
import com.app.currencyconverter.helpers.Resource
import com.app.currencyconverter.helpers.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val getLastCurrencyRatesUseCase: GetLastCurrencyRatesUseCase,
    private val performCurrencyConversionUseCase: PerformCurrencyConversionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<CurrencyItem>>>(UiState.Loading())
    val uiState = _uiState.asStateFlow()

    private val _conversionResult = MutableStateFlow(DEFAULT_CONVERSION_VALUE)
    val conversionResult = _conversionResult.asStateFlow()

    private var actualCurrencyRates: List<CurrencyItem> = emptyList()

    fun loadData() {
        viewModelScope.launch(dispatchers.io) {
            when (val result = getLastCurrencyRatesUseCase()) {
                is Resource.Success -> {
                    _uiState.value =
                        UiState.Success(content = result.content, mode = result.mode)
                    actualCurrencyRates = result.content
                }
                is Resource.Failure -> {
                    _uiState.value = UiState.Error(error = result.error)
                }
            }
        }
    }

    fun convert(amountString: String, fromCurrencyCode: String, toCurrencyCode: String) {
        val result = performCurrencyConversionUseCase(
            amountString = amountString,
            currencyItems = actualCurrencyRates,
            fromCurrencyCode = fromCurrencyCode,
            toCurrencyCode = toCurrencyCode
        )
        _conversionResult.value = result
    }
}