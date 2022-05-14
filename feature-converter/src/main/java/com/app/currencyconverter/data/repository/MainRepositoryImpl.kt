package com.app.currencyconverter.data.repository

import android.content.SharedPreferences
import com.app.currencyconverter.constants.CommonConstants.DATE_PATTERN
import com.app.currencyconverter.constants.CommonConstants.UPDATE_DATE_KEY
import com.app.currencyconverter.data.local.dao.CurrencyDao
import com.app.currencyconverter.data.local.entities.CurrencyEntity
import com.app.currencyconverter.data.mappers.CurrencyMapper
import com.app.currencyconverter.data.remote.api.CurrencyApi
import com.app.currencyconverter.data.remote.dto.CurrencyDto
import com.app.currencyconverter.domain.models.CurrencyItem
import com.app.currencyconverter.domain.repository.MainRepository
import com.app.currencyconverter.extensions.parseAsDate
import com.app.currencyconverter.helpers.DataResponse
import com.app.currencyconverter.helpers.ErrorType
import com.app.currencyconverter.helpers.Mode
import com.app.currencyconverter.helpers.Resource
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val currencyDao: CurrencyDao,
    private val currencyApi: CurrencyApi
) : MainRepository {

    override suspend fun getLastCurrencyRates(): Resource<List<CurrencyItem>> {
        return when (val remoteData = performNetworkRequest()) {

            is DataResponse.Success -> {
                remoteData.content.saveToDatabase()
                val result = remoteData.content.map { CurrencyMapper.toDomainModelFromDto(it) }
                Resource.Success(content = result, mode = Mode.ONLINE)
            }

            is DataResponse.Failure -> {
                when (val localData = performDatabaseRequest()) {

                    is DataResponse.Success -> {
                        val result =
                            localData.content.map { CurrencyMapper.toDomainModelFromEntity(it) }
                        Resource.Success(content = result, mode = Mode.OFFLINE)
                    }

                    is DataResponse.Failure -> {
                        Resource.Failure(error = localData.error)
                    }
                }
            }
        }
    }

    private suspend fun performNetworkRequest(): DataResponse<List<CurrencyDto>> {
        try {
            val response = currencyApi.loadCurrencyList()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val remoteData = body.first()
                    updateEffectiveDate(remoteData.effectiveDate)
                    return DataResponse.Success(content = remoteData.currencyRates)
                }
                return DataResponse.Failure(error = ErrorType.UNKNOWN)
            }
            return DataResponse.Failure(error = ErrorType.NETWORK)
        } catch (e: Exception) {
            return DataResponse.Failure(error = ErrorType.NETWORK)
        }
    }

    private fun updateEffectiveDate(effectiveDate: String) {
        sharedPrefs.edit().run {
            putString(UPDATE_DATE_KEY, effectiveDate.parseAsDate(DATE_PATTERN))
            apply()
        }
    }

    private fun List<CurrencyDto>.saveToDatabase() {
        this.forEach { dto ->
            currencyDao.insertCurrency(CurrencyMapper.toEntityFromDto(dto))
        }
    }

    private fun performDatabaseRequest(): DataResponse<List<CurrencyEntity>> {
        val localData = currencyDao.getCurrencyList()
        if (localData.isEmpty()) {
            return DataResponse.Failure(error = ErrorType.DATABASE)
        }
        return DataResponse.Success(content = localData)
    }
}