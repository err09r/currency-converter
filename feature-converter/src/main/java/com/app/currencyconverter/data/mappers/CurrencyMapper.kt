package com.app.currencyconverter.data.mappers

import com.app.currencyconverter.domain.mappers.ObjectMapper
import com.app.currencyconverter.data.local.entities.CurrencyEntity
import com.app.currencyconverter.domain.models.CurrencyItem
import com.app.currencyconverter.data.remote.dto.CurrencyDto

object CurrencyMapper : ObjectMapper<CurrencyDto, CurrencyItem, CurrencyEntity> {

    override fun toEntityFromDto(dto: CurrencyDto): CurrencyEntity {
        with(dto) {
            return CurrencyEntity(
                currencyTitle = currencyTitle,
                currencyCode = currencyCode,
                rate = rate
            )
        }
    }

    override fun toDomainModelFromDto(dto: CurrencyDto): CurrencyItem {
        with(dto) {
            return CurrencyItem(
                currencyTitle = currencyTitle,
                currencyCode = currencyCode,
                rate = rate
            )
        }
    }

    override fun toDomainModelFromEntity(entity: CurrencyEntity): CurrencyItem {
        with(entity) {
            return CurrencyItem(
                currencyTitle = currencyTitle,
                currencyCode = currencyCode,
                rate = rate
            )
        }
    }
}