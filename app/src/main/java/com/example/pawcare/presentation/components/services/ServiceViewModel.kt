package com.example.pawcare.presentation.components.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.services.GetServicesUseCase
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val getServicesUseCase: GetServicesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceUiState())
    val state = _state.asStateFlow()

    init {
        getServices()
    }

    fun onEvent(event: ServiceUiEvent) {
        when (event) {
            is ServiceUiEvent.Refresh -> getServices()
        }
    }

    private fun getServices() {
        getServicesUseCase().onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        currentState.copy(
                            services = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        currentState.copy(
                            services = result.data ?: currentState.services,
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