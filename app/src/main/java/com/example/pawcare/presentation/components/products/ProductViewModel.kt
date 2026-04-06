package com.example.pawcare.presentation.components.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.products.GetProductsUseCase
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ProductUiState())
    val state = _state.asStateFlow()

    init {
        loadProducts()
    }

    fun onEvent(event: ProductUiEvent) {
        if (event is ProductUiEvent.OnCategorySelected) {
            _state.update { it.copy(selectedCategory = event.category) }
            val filter = if (event.category == "Todos") null else event.category
            loadProducts(filter)
        }
    }

    private fun loadProducts(category: String? = null) {
        getProductsUseCase(category).onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        currentState.copy(
                            products = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        currentState.copy(
                            products = result.data ?: currentState.products,
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        currentState.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}