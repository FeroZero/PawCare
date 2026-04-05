package com.example.pawcare.domain.model

data class Pet(
    val id: String,
    val name: String,
    val breed: String,
    val age: Int,
    val photoUrl: String?,
    val createdAt: String,
    val ownerId: String,
    val ownerName: String
)
