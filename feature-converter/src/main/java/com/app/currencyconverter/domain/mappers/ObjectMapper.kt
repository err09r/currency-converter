package com.app.currencyconverter.domain.mappers

interface ObjectMapper<T, S, U> {

    fun toEntityFromDto(dto: T): U

    fun toDomainModelFromDto(dto: T): S

    fun toDomainModelFromEntity(entity: U): S
}