package com.example.pawcare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OwnerDto(
    @SerializedName("id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("address") val address: String,
    @SerializedName("isVip") val isVip: Boolean,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("pets") val pets: List<PetDto>? = null
)

data class CreateOwnerRequest(
    val fullName: String,
    val phone: String,
    val email: String,
    val address: String,
    val isVip: Boolean = false
)
