package com.example.pawcare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PetDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("breed") val breed: String,
    @SerializedName("age") val age: Int,
    @SerializedName("photoUrl") val photoUrl: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("owner") val owner: OwnerDto? = null
)

data class CreatePetRequest(
    val name: String,
    val breed: String,
    val age: Int,
    val photoUrl: String?,
    val ownerId: String
)
