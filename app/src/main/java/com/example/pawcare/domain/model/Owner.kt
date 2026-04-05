package com.example.pawcare.domain.model

data class Owner(
    val id: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val address: String,
    val isVip: Boolean,
    val createdAt: String,
    val pets: List<Pet> = emptyList()
)
