package com.example.pawcare.domain.model

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val imageUrl: String?,
    val stock: Int
)
