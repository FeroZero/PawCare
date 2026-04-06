package com.example.pawcare.domain.use_case.products

import com.example.pawcare.domain.model.Product
import com.example.pawcare.domain.repository.ProductRepository
import com.example.pawcare.domain.util.Resource
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        name: String,
        category: String,
        price: Double,
        imageUrl: String?,
        stock: Int
    ): Resource<Product> = repository.createProduct(name, category, price, imageUrl, stock)
}