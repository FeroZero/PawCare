package com.example.pawcare.presentation.components.owners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.owners.GetOwnersUseCase
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OwnerViewModel @Inject constructor(
    private val getOwnersUseCase: GetOwnersUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(OwnerUiState())
    val state = _state.asStateFlow()

    init { loadOwners() }

    private fun loadOwners() {
        getOwnersUseCase().onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        currentState.copy(
                            owners = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        currentState.copy(
                            owners = result.data ?: currentState.owners,
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