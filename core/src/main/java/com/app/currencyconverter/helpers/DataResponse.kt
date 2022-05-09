package com.app.currencyconverter.helpers

sealed class DataResponse<T> {
    class Success<T>(val content: T) : DataResponse<T>()
    class Failure<T>(val error: ErrorType) : DataResponse<T>()
}