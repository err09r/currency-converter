package com.app.currencyconverter.data.mappers

import com.app.currencyconverter.data.local.entities.CurrencyEntity
import com.app.currencyconverter.data.remote.dto.CurrencyDto
import com.app.currencyconverter.domain.models.CurrencyItem
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CurrencyMapperTest {

    @Test
    fun `Check if mapper correctly maps to entity from dto`() {
        val testDto = CurrencyDto(currencyTitle = "title", currencyCode = "XXX", rate = 1.0)
        val expectedEntity = CurrencyEntity(
            currencyTitle = testDto.currencyTitle,
            currencyCode = testDto.currencyCode,
            rate = testDto.rate
        )
        val actualEntity = CurrencyMapper.toEntityFromDto(testDto)
        assertThat(actualEntity).isEqualTo(expectedEntity)
    }

    @Test
    fun `Check if mapper correctly maps to domain model from dto`() {
        val testDto = CurrencyDto(currencyTitle = "title", currencyCode = "XXX", rate = 1.0)
        val expectedItem = CurrencyItem(
            currencyTitle = testDto.currencyTitle,
            currencyCode = testDto.currencyCode,
            rate = testDto.rate
        )
        val actualEntity = CurrencyMapper.toDomainModelFromDto(testDto)
        assertThat(actualEntity).isEqualTo(expectedItem)
    }

    @Test
    fun `Check if mapper correctly maps to domain model from entity`() {
        val testEntity = CurrencyEntity(currencyTitle = "title", currencyCode = "XXX", rate = 1.0)
        val expectedItem = CurrencyItem(
            currencyTitle = testEntity.currencyTitle,
            currencyCode = testEntity.currencyCode,
            rate = testEntity.rate
        )
        val actualItem = CurrencyMapper.toDomainModelFromEntity(testEntity)
        assertThat(actualItem).isEqualTo(expectedItem)
    }
}