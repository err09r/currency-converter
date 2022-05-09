package com.app.currencyconverter.data.repository

import android.content.SharedPreferences
import com.app.currencyconverter.data.local.dao.CurrencyDao
import com.app.currencyconverter.data.local.entities.CurrencyEntity
import com.app.currencyconverter.data.remote.api.CurrencyApi
import com.app.currencyconverter.data.remote.api.CurrencyApiResponse
import com.app.currencyconverter.data.remote.dto.CurrencyDto
import com.app.currencyconverter.domain.repository.MainRepository
import com.app.currencyconverter.helpers.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import retrofit2.Response

@ExperimentalCoroutinesApi
class MainRepositoryImplTest {

    private lateinit var repository: MainRepository
    private lateinit var successfulResponse: Response<List<CurrencyApiResponse>>
    private lateinit var errorResponse: Response<List<CurrencyApiResponse>>
    private lateinit var testResponse: List<CurrencyApiResponse>
    private val testDto = CurrencyDto("", "XXX", 1.0)
    private val testEntities = listOf(CurrencyEntity("", "XXX", 1.0))

    private val currencyDao = mock<CurrencyDao>()
    private val currencyApi = mock<CurrencyApi>()
    private val sharedPrefs = mock<SharedPreferences>()
    private val sharedPrefsEditor = mock<SharedPreferences.Editor>()
    private val errorResponseBody = mock<ResponseBody>()


    @Before
    fun setUp() {
        repository = MainRepositoryImpl(
            sharedPrefs = sharedPrefs,
            currencyDao = currencyDao,
            currencyApi = currencyApi
        )

        testResponse = listOf(
            CurrencyApiResponse(
                table = "",
                number = "",
                effectiveDate = "",
                currencyRates = listOf(testDto)
            )
        )

        successfulResponse = Response.success(testResponse)
        errorResponse = Response.error(404, errorResponseBody)

        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)
    }

    @After
    fun tearDown() {
        Mockito.reset(currencyDao, currencyApi, sharedPrefs, sharedPrefsEditor, errorResponseBody)
    }

    @Test
    fun `Check if returned resource is success when network response is successful`() = runTest {
        Mockito.`when`(currencyApi.loadCurrencyList()).thenReturn(successfulResponse)

        val actualResource = repository.getLastCurrencyRates()

        assertThat(actualResource is Resource.Success).isTrue()
        Mockito.verify(currencyApi).loadCurrencyList()
    }

    @Test
    fun `Check if returned resource is failure when network response is error`() = runTest {
        Mockito.`when`(currencyApi.loadCurrencyList()).thenReturn(errorResponse)

        val actualResource = repository.getLastCurrencyRates()

        assertThat(actualResource is Resource.Failure).isTrue()
        Mockito.verify(currencyApi).loadCurrencyList()
    }

    @Test
    fun `Check if returned resource is success when database response is successful`() = runTest {
        Mockito.`when`(currencyApi.loadCurrencyList()).thenReturn(errorResponse)
        Mockito.`when`(currencyDao.getCurrencyList()).thenReturn(testEntities)

        val actualResource = repository.getLastCurrencyRates()

        assertThat(actualResource is Resource.Success).isTrue()
        Mockito.verify(currencyApi).loadCurrencyList()
        Mockito.verify(currencyDao).getCurrencyList()
    }

    @Test
    fun `Check if returned resource is failure when database response is error`() = runTest {
        Mockito.`when`(currencyApi.loadCurrencyList()).thenReturn(errorResponse)
        Mockito.`when`(currencyDao.getCurrencyList()).thenReturn(emptyList())

        val actualResource = repository.getLastCurrencyRates()

        assertThat(actualResource is Resource.Failure).isTrue()
        Mockito.verify(currencyApi).loadCurrencyList()
        Mockito.verify(currencyDao).getCurrencyList()
    }
}