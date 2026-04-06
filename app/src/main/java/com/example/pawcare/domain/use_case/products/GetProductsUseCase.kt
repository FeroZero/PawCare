package com.example.pawcare.domain.use_case.products

import com.example.pawcare.domain.model.Product
import com.example.pawcare.domain.repository.ProductRepository
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(category: String? = null): Flow<Resource<List<Product>>> =
        repository.getProducts(category)
}