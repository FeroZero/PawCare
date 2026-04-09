package com.example.pawcare.presentation.components.products

sealed interface ProductUiEvent {
    data class OnCategorySelected(val category: String?) : ProductUiEvent
}