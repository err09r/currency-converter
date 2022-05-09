package com.app.currencyconverter.extensions

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Date

class CommonExtensionsTest {

    @Test
    fun `Check if valid date string formatting is correct`() {
        val testString = "2022-01-01"
        val actual = testString.parseAsDate(pattern = "dd.MM.yyyy")
        val expected = "01.01.2022"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Check if invalid date string formatting returns same value`() {
        val testString = ""
        val actual = testString.parseAsDate(pattern = "dd.MM.yyyy")
        assertThat(actual).isEqualTo(testString)
    }

    @Test
    fun `Check if date parsed to string is correct`() {
        val testDate = Date()
        val actual = testDate.formatToString("dd.MM.yyyy")
        assertThat(actual).containsMatch("\\d{2}\\.\\d{2}\\.\\d{4}")
    }
}