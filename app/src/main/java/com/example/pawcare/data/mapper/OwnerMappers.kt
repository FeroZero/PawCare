package com.example.pawcare.data.mapper

import com.example.pawcare.data.local.entity.OwnerEntity
import com.example.pawcare.data.remote.dto.OwnerDto
import com.example.pawcare.domain.model.Owner

fun OwnerDto.toOwner(): Owner {
    return Owner(
        id = id,
        fullName = fullName,
        phone = phone,
        email = email,
        address = address,
        isVip = isVip,
        createdAt = createdAt,
        pets = pets?.map { it.toPet() } ?: emptyList()
    )
}

fun OwnerDto.toEntity(): OwnerEntity {
    return OwnerEntity(
        id = id,
        fullName = fullName,
        phone = phone,
        email = email,
        address = address,
        isVip = isVip,
        createdAt = createdAt
    )
}

fun OwnerEntity.toOwner(): Owner {
    return Owner(
        id = id,
        fullName = fullName,
        phone = phone,
        email = email,
        address = address,
        isVip = isVip,
        createdAt = createdAt
    )
}
