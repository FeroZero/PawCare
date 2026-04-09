package com.example.pawcare.presentation.components.products

import com.example.pawcare.domain.model.Product

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)