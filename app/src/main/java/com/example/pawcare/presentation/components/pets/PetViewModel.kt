package com.example.pawcare.presentation.components.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.pets.*
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
class PetViewModel @Inject constructor(
    private val getPetsUseCase: GetPetsUseCase,
    private val searchPetsUseCase: SearchPetsUseCase,
    private val createPetUseCase: CreatePetUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
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

            is PetUiEvent.OnNameChange -> _state.update { it.copy(name = event.value) }
            is PetUiEvent.OnBreedChange -> _state.update { it.copy(breed = event.value) }
            is PetUiEvent.OnAgeChange -> _state.update { it.copy(age = event.value) }
            is PetUiEvent.OnOwnerIdChange -> _state.update { it.copy(ownerId = event.value) }
            is PetUiEvent.OnPhotoUrlChange -> _state.update { it.copy(photoUrl = event.value) }

            PetUiEvent.OnSavePetClick -> savePet()

            is PetUiEvent.OnDeletePet -> {
            }

            is PetUiEvent.OnPetClick -> {
            }

            is PetUiEvent.OnUpdatePetClick -> {
            }
        }
    }

    private fun getPets() {
        getPetsUseCase().onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> currentState.copy(
                        pets = result.data,
                        isLoading = false,
                        error = null,
                        totalRegistered = result.data.size
                    )
                    is Resource.Error -> currentState.copy(
                        pets = result.data ?: currentState.pets,
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

    private fun searchPets(query: String) {
        searchPetsUseCase(query).onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> currentState.copy(
                        pets = result.data,
                        isLoading = false
                    )
                    is Resource.Error -> currentState.copy(
                        pets = result.data ?: currentState.pets,
                        isLoading = false
                    )
                    is Resource.Loading -> currentState.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun savePet() {
        val currentState = _state.value
        val ageInt = currentState.age.toIntOrNull() ?: 0

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val result = createPetUseCase(
                name = currentState.name,
                breed = currentState.breed,
                age = ageInt,
                photoUrl = currentState.photoUrl,
                ownerId = currentState.ownerId
            )

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isSaving = false, saveSuccess = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isSaving = false, error = result.message) }
                }
                else -> {}
            }
        }
    }
}
