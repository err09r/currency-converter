package com.app.currencyconverter.helpers

sealed class UiState<T> {
    class Loading<T> : UiState<T>()
    class Success<T>(val content: T, val mode: Mode) : UiState<T>()
    class Error<T>(val error: ErrorType) : UiState<T>()
}