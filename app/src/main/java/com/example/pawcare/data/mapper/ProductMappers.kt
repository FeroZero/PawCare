package com.example.pawcare.data.mapper

import com.example.pawcare.data.local.entity.ProductEntity
import com.example.pawcare.data.remote.dto.ProductDto
import com.example.pawcare.domain.model.Product

fun ProductDto.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        category = category,
        price = price,
        imageUrl = imageUrl,
        stock = stock
    )
}

fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        category = category,
        price = price,
        imageUrl = imageUrl,
        stock = stock
    )
}

fun ProductEntity.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        category = category,
        price = price,
        imageUrl = imageUrl,
        stock = stock
    )
}
