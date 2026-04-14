package com.example.pawcare.presentation.components.products

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.model.Product
import com.example.pawcare.domain.use_case.products.CreateProductUseCase
import com.example.pawcare.domain.use_case.products.DeleteProductsUseCase // Importa el nombre correcto
import com.example.pawcare.domain.use_case.products.GetProductsUseCase
import com.example.pawcare.domain.util.Resource
import com.example.pawcare.presentation.screens.product.InventoryListScreen
import com.example.pawcare.ui.theme.PawCareTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val deleteProductUseCase: DeleteProductsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductUiState())
    val state = _state.asStateFlow()

    init {
        loadProducts()
    }

    fun onEvent(event: ProductUiEvent) {
        when (event) {
            is ProductUiEvent.OnEditProductClick -> {
                _state.update { it.copy(
                    name = event.product.name,
                    price = event.product.price.toString(),
                    category = event.product.category,
                    stock = event.product.stock.toString(),
                    error = null
                ) }
            }

            is ProductUiEvent.OnDeleteProduct -> {
                deleteProduct(event.id)
            }

            ProductUiEvent.OnClearForm -> {
                _state.update { it.copy(name = "", price = "", category = "", stock = "", saveSuccess = false) }
            }

            is ProductUiEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
            }

            is ProductUiEvent.Refresh -> {
                loadProducts(_state.value.selectedCategory)
            }

            is ProductUiEvent.OnNameChange -> {
                _state.update { it.copy(name = event.value) }
            }

            is ProductUiEvent.OnPriceChange -> {
                _state.update { it.copy(price = event.value) }
            }

            is ProductUiEvent.OnCategoryChange -> {
                _state.update { it.copy(category = event.value) }
            }

            is ProductUiEvent.OnImageUrlChange -> {
                _state.update { it.copy(imageUrl = event.value) }
            }

            is ProductUiEvent.OnStockChange -> {
                _state.update { it.copy(stock = event.value) }
            }

            is ProductUiEvent.OnSaveProductClick -> {
                saveProduct()
            }

            is ProductUiEvent.OnProductClick -> {
            }
        }
    }

    private fun loadProducts(category: String? = null) {
        getProductsUseCase(category).onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        currentState.copy(
                            products = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        // Eliminamos la llamada redundante a _state.update aquí
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

    private fun saveProduct() {
        val currentState = _state.value

        // Validaciones básicas antes de enviar
        val priceDouble = currentState.price.toDoubleOrNull() ?: 0.0
        val stockInt = currentState.stock.toIntOrNull() ?: 0

        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = "El nombre no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val result = createProductUseCase(
                name = currentState.name,
                category = currentState.category,
                price = priceDouble,
                imageUrl = currentState.imageUrl,
                stock = stockInt
            )

            when (result) {
                is Resource.Success -> {
                    // Importante: saveSuccess disparará el onBack() en la Screen
                    _state.update { it.copy(
                        isSaving = false,
                        saveSuccess = true
                    ) }
                }

                is Resource.Error -> {
                    _state.update { it.copy(
                        isSaving = false,
                        error = result.message
                    ) }
                }

                is Resource.Loading -> { }
            }
        }
    }

    private fun deleteProduct(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = deleteProductUseCase(id)

            if (result is Resource.Error) {
                _state.update { it.copy(
                    error = result.message,
                    isLoading = false
                ) }
            } else if (result is Resource.Success) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}