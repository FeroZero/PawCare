package com.example.pawcare.presentation.components.owners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.owners.CreateOwnerUseCase
import com.example.pawcare.domain.use_case.owners.DeleteOwnerUseCase // Asegúrate de que este import exista
import com.example.pawcare.domain.use_case.owners.GetOwnerByIdUseCase
import com.example.pawcare.domain.use_case.owners.GetOwnersUseCase
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
class OwnerViewModel @Inject constructor(
    private val getOwnerByIdUseCase: GetOwnerByIdUseCase,
    private val getOwnersUseCase: GetOwnersUseCase,
    private val createOwnerUseCase: CreateOwnerUseCase,
    private val deleteOwnerUseCase: DeleteOwnerUseCase // 1. INYECTAMOS EL USE CASE
) : ViewModel() {

    private val _state = MutableStateFlow(OwnerUiState())
    val state = _state.asStateFlow()

    init {
        loadOwners()
    }

    fun onEvent(event: OwnerUiEvent) {
        when (event) {
            is OwnerUiEvent.OnOwnerClick -> {
                loadOwnerById(event.ownerId)
            }

            is OwnerUiEvent.Refresh -> loadOwners()

            is OwnerUiEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
            }

            is OwnerUiEvent.OnFullNameChange -> {
                _state.update { it.copy(fullName = event.value) }
            }

            is OwnerUiEvent.OnPhoneChange -> {
                _state.update { it.copy(phone = event.value) }
            }

            is OwnerUiEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.value) }
            }

            is OwnerUiEvent.OnAddressChange -> {
                _state.update { it.copy(address = event.value) }
            }

            is OwnerUiEvent.OnVipStatusChange -> {
                _state.update { it.copy(isVip = event.isVip) }
            }

            is OwnerUiEvent.OnDeleteOwner -> {
                deleteOwner(event.ownerId)
            }

            OwnerUiEvent.OnSaveOwnerClick -> saveOwner()
        }
    }

    private fun deleteOwner(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = deleteOwnerUseCase(id)

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    loadOwners()
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> { }
            }
        }
    }

    private fun loadOwners() {
        getOwnersUseCase().onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> currentState.copy(
                        owners = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )

                    is Resource.Error -> currentState.copy(
                        owners = result.data ?: currentState.owners,
                        isLoading = false,
                        error = result.message
                    )

                    is Resource.Loading -> currentState.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun saveOwner() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val result = createOwnerUseCase(
                fullName = currentState.fullName,
                phone = currentState.phone,
                email = currentState.email,
                address = currentState.address,
                isVip = currentState.isVip
            )

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isSaving = false, saveSuccess = true) }
                }

                is Resource.Error -> {
                    _state.update { it.copy(isSaving = false, error = result.message) }
                }

                is Resource.Loading -> {}
            }
        }
    }

    private fun loadOwnerById(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = getOwnerByIdUseCase(id)

            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        currentState.copy(
                            selectedOwner = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        currentState.copy(
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
        }
    }
}