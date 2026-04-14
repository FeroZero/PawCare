package com.example.pawcare.domain.use_case.products

import com.example.pawcare.domain.repository.ProductRepository
import com.example.pawcare.domain.util.Resource
import javax.inject.Inject

class DeleteProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        return repository.deleteProduct(id)
    }
}