package com.example.pawcare.data.repository

import com.example.pawcare.data.local.dao.ProductDao
import com.example.pawcare.data.mapper.toEntity
import com.example.pawcare.data.mapper.toProduct
import com.example.pawcare.data.remote.PawCareApiService
import com.example.pawcare.data.remote.SafeApiCall
import com.example.pawcare.data.remote.dto.CreateProductRequest
import com.example.pawcare.domain.model.Product
import com.example.pawcare.domain.repository.ProductRepository
import com.example.pawcare.domain.util.Resource
import com.example.pawcare.domain.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: PawCareApiService,
    private val productDao: ProductDao
) : ProductRepository, SafeApiCall {

    override fun getProducts(category: String?): Flow<Resource<List<Product>>> = networkBoundResource(
        query = {
            if (category != null) {
                productDao.getProductsByCategory(category).map { entities -> entities.map { it.toProduct() } }
            } else {
                productDao.getAllProducts().map { entities -> entities.map { it.toProduct() } }
            }
        },
        fetch = { api.getProducts(category).body() ?: emptyList() },
        saveFetchResult = { dtos ->
            productDao.upsertProducts(dtos.map { it.toEntity() })
        }
    )

    override suspend fun createProduct(
        name: String,
        category: String,
        price: Double,
        imageUrl: String?,
        stock: Int
    ): Resource<Product> {
        val result = safeApiCall {
            api.createProduct(CreateProductRequest(name, category, price, imageUrl, stock))
        }
        return when (result) {
            is Resource.Success -> {
                productDao.upsertProducts(listOf(result.data.toEntity()))
                Resource.Success(result.data.toProduct())
            }
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading()
        }
    }
}
