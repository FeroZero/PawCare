package com.example.pawcare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ServiceDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("durationMinutes") val durationMinutes: Int
)
