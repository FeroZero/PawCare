package com.example.pawcare.domain.repository

import com.example.pawcare.domain.model.Product
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(category: String?): Flow<Resource<List<Product>>>
    suspend fun createProduct(name: String, category: String, price: Double, imageUrl: String?, stock: Int): Resource<Product>
}
