package com.example.pawcare.presentation.components.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.pets.GetPetsUseCase
import com.example.pawcare.domain.use_case.pets.SearchPetsUseCase
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val getPetsUseCase: GetPetsUseCase,
    private val searchPetsUseCase: SearchPetsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(PetUiState())
    val state = _state.asStateFlow()

    init {
        getPets()
    }

    fun onEvent(event: PetUiEvent) {
        when (event) {
            is PetUiEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
                searchPets(event.query)
            }
            PetUiEvent.Refresh -> getPets()
        }
    }

    private fun getPets() {
        getPetsUseCase().onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        currentState.copy(
                            pets = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        currentState.copy(
                            pets = result.data ?: currentState.pets,
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

    private fun searchPets(query: String) {
        searchPetsUseCase(query).onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        currentState.copy(
                            pets = result.data,
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        currentState.copy(
                            pets = result.data ?: currentState.pets,
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        currentState.copy(isLoading = true)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}
