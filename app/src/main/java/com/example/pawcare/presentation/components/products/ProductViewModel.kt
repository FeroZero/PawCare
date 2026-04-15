package com.example.pawcare.presentation.components.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.products.CreateProductUseCase
import com.example.pawcare.domain.use_case.products.DeleteProductsUseCase
import com.example.pawcare.domain.use_case.products.GetProductsUseCase
import com.example.pawcare.domain.util.Resource
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
    private val deleteProductUseCase: DeleteProductsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ProductUiState())
    val state = _state.asStateFlow()

    init {
        loadProducts()

        // Observamos cuando los productos llegan para auto-seleccionar el que queremos editar
        viewModelScope.launch {
            state.collect { currentState ->
                val productId = savedStateHandle.get<String>("productId")
                if (productId != null && currentState.productId == null && currentState.products.isNotEmpty()) {
                    loadProductById(productId)
                }
            }
        }
    }

    private fun loadProductById(id: String) {
        val product = _state.value.products.find { it.id == id }

        if (product != null) {
            _state.update { it.copy(
                productId = product.id,
                name = product.name,
                category = product.category,
                price = product.price.toString(),
                stock = product.stock.toString(),
                imageUrl = product.imageUrl,
                isLoading = false,
                error = null
            ) }
        } else {
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onEvent(event: ProductUiEvent) {
        when (event) {
            is ProductUiEvent.OnEditProductClick -> {
                _state.update { it.copy(
                    productId = event.product.id,
                    name = event.product.name,
                    price = event.product.price.toString(),
                    category = event.product.category,
                    stock = event.product.stock.toString(),
                    imageUrl = event.product.imageUrl,
                    saveSuccess = false,
                    error = null
                ) }
            }

            is ProductUiEvent.OnClearForm -> {
                _state.update { it.copy(
                    productId = null,
                    name = "",
                    price = "",
                    category = "",
                    stock = "",
                    imageUrl = null,
                    saveSuccess = false,
                    error = null
                ) }
            }

            is ProductUiEvent.OnDeleteProduct -> {
                deleteProduct(event.id)
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
        val productIdToEdit = currentState.productId

        val priceDouble = currentState.price.toDoubleOrNull() ?: 0.0
        val stockInt = currentState.stock.toIntOrNull() ?: 0

        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = "El nombre no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            if (productIdToEdit != null) {
                try {
                    deleteProductUseCase(productIdToEdit)
                } catch (e: Exception) {
                    println("DEBUG: Error de borrado ignorado: ${e.message}")
                }
            }

            val result = createProductUseCase(
                name = currentState.name,
                category = currentState.category,
                price = priceDouble,
                imageUrl = currentState.imageUrl,
                stock = stockInt
            )

            when (result) {
                is Resource.Success -> {
                    println("DEBUG: Proceso completado con éxito.")
                    _state.update { it.copy(
                        isSaving = false,
                        saveSuccess = true,
                        productId = null
                    ) }
                    loadProducts()
                }
                is Resource.Error -> {
                    println("DEBUG: Error en la creación final: ${result.message}")
                    _state.update { it.copy(
                        isSaving = false,
                        error = "Error al guardar el producto: ${result.message}"
                    ) }
                }
                else -> {}
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