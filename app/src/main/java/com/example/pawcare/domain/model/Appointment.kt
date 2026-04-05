package com.example.pawcare.domain.model

data class Appointment(
    val id: String,
    val date: String,
    val timeSlot: String,
    val status: String,
    val totalPrice: Double,
    val paymentMethod: String?,
    val notes: String?,
    val petId: String,
    val petName: String,
    val petPhotoUrl: String?,
    val services: List<Service>
)
