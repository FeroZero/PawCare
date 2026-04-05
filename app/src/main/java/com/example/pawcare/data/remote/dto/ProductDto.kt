package com.example.pawcare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("price") val price: Double,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("stock") val stock: Int
)

data class CreateProductRequest(
    val name: String,
    val category: String,
    val price: Double,
    val imageUrl: String?,
    val stock: Int
)
