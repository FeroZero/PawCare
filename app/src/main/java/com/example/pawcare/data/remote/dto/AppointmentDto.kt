package com.example.pawcare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AppointmentDto(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("timeSlot") val timeSlot: String,
    @SerializedName("status") val status: String,
    @SerializedName("totalPrice") val totalPrice: Double,
    @SerializedName("paymentMethod") val paymentMethod: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("pet") val pet: PetDto,
    @SerializedName("services") val services: List<ServiceDto>
)

data class CreateAppointmentRequest(
    val date: String,
    val timeSlot: String,
    val petId: String,
    val serviceIds: List<String>,
    val notes: String? = null
)

data class UpdateAppointmentStatusRequest(
    val status: String,
    val paymentMethod: String? = null
)
