package com.example.pawcare.data.mapper

import com.example.pawcare.data.local.entity.PetEntity
import com.example.pawcare.data.remote.dto.PetDto
import com.example.pawcare.domain.model.Pet

fun PetDto.toPet(): Pet {
    return Pet(
        id = id,
        name = name,
        breed = breed,
        age = age,
        photoUrl = photoUrl,
        createdAt = createdAt,
        ownerId = owner?.id ?: "",
        ownerName = owner?.fullName ?: ""
    )
}

fun PetDto.toEntity(): PetEntity {
    return PetEntity(
        id = id,
        name = name,
        breed = breed,
        age = age,
        photoUrl = photoUrl,
        ownerId = owner?.id ?: "",
        ownerName = owner?.fullName ?: "",
        createdAt = createdAt
    )
}

fun PetEntity.toPet(): Pet {
    return Pet(
        id = id,
        name = name,
        breed = breed,
        age = age,
        photoUrl = photoUrl,
        createdAt = createdAt,
        ownerId = ownerId,
        ownerName = ownerName
    )
}
