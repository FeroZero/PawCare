package com.example.pawcare.presentation.components.products

import com.example.pawcare.domain.model.Product

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val name: String = "",
    val price: String = "",
    val category: String = "",
    val imageUrl: String? = null,
    val stock: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)