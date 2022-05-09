package com.app.currencyconverter.extensions

import com.app.currencyconverter.constants.CommonConstants.API_DATE_PATTERN
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.parseAsDate(pattern: String): String {
    return try {
        val format = SimpleDateFormat(API_DATE_PATTERN, Locale.US)
        val newDate = format.parse(this)
        newDate?.formatToString(pattern) ?: this
    } catch (e: Exception) {
        this
    }
}

fun Date.formatToString(pattern: String): String = SimpleDateFormat(pattern, Locale.US).format(this)