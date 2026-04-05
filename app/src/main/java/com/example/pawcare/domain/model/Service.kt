package com.example.pawcare.domain.model

data class Service(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val durationMinutes: Int
)
