package com.example.pawcare.presentation.components.products

import com.example.pawcare.domain.model.Product

sealed interface ProductUiEvent {
    data class OnSearchQueryChange(val query: String) : ProductUiEvent
    object Refresh : ProductUiEvent
    data class OnNameChange(val value: String) : ProductUiEvent
    data class OnPriceChange(val value: String) : ProductUiEvent
    data class OnCategoryChange(val value: String) : ProductUiEvent
    data class OnStockChange(val value: String) : ProductUiEvent
    data class OnImageUrlChange(val value: String?) : ProductUiEvent
    data class OnProductClick(val id: String) : ProductUiEvent
    object OnSaveProductClick : ProductUiEvent
    data class OnDeleteProduct(val id: String) : ProductUiEvent
    data class OnEditProductClick(val product: Product) : ProductUiEvent
    object OnClearForm : ProductUiEvent
}