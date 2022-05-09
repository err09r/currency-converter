package com.app.currencyconverter.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.app.currencyconverter.constants.CommonConstants.PREFERENCES_FILE_KEY
import com.app.currencyconverter.data.local.dao.CurrencyDao
import com.app.currencyconverter.data.local.database.CurrencyDatabase
import com.app.currencyconverter.data.remote.api.CurrencyApi
import com.app.currencyconverter.data.repository.MainRepositoryImpl
import com.app.currencyconverter.domain.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

private const val DATABASE_NAME = "currency_database"

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        sharedPreferences: SharedPreferences,
        currencyDao: CurrencyDao,
        currencyApi: CurrencyApi
    ): MainRepository {
        return MainRepositoryImpl(
            sharedPrefs = sharedPreferences,
            currencyDao = currencyDao,
            currencyApi = currencyApi
        )
    }

    @Singleton
    @Provides
    fun provideCurrencyApi(retrofit: Retrofit): CurrencyApi {
        return retrofit.create(CurrencyApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCurrencyDatabase(@ApplicationContext context: Context): CurrencyDatabase {
        return Room.databaseBuilder(
            context,
            CurrencyDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideCurrencyDao(database: CurrencyDatabase): CurrencyDao = database.currencyDao()
}