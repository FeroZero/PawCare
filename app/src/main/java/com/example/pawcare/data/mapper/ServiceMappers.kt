package com.example.pawcare.data.mapper

import com.example.pawcare.data.local.entity.ServiceEntity
import com.example.pawcare.data.remote.dto.ServiceDto
import com.example.pawcare.domain.model.Service

fun ServiceDto.toService(): Service {
    return Service(
        id = id,
        name = name,
        description = description,
        price = price,
        durationMinutes = durationMinutes
    )
}

fun ServiceDto.toEntity(): ServiceEntity {
    return ServiceEntity(
        id = id,
        name = name,
        description = description,
        price = price,
        durationMinutes = durationMinutes
    )
}

fun ServiceEntity.toService(): Service {
    return Service(
        id = id,
        name = name,
        description = description,
        price = price,
        durationMinutes = durationMinutes
    )
}
