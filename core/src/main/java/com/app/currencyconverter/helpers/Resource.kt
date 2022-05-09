package com.app.currencyconverter.helpers

sealed class Resource<T> {
    class Success<T>(val content: T, val mode: Mode) : Resource<T>()
    class Failure<T>(val error: ErrorType) : Resource<T>()
}