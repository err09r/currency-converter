package com.app.currencyconverter.di

import com.app.currencyconverter.domain.repository.MainRepository
import com.app.currencyconverter.domain.usecases.GetLastCurrencyRatesUseCase
import com.app.currencyconverter.domain.usecases.PerformCurrencyConversionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    fun providePerformCurrencyConversionUseCase(): PerformCurrencyConversionUseCase {
        return PerformCurrencyConversionUseCase()
    }

    @Provides
    fun provideGetLastCurrencyRatesUseCase(repository: MainRepository): GetLastCurrencyRatesUseCase {
        return GetLastCurrencyRatesUseCase(repository = repository)
    }
}