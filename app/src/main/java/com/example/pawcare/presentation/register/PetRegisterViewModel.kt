package com.example.pawcare.presentation.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.model.Pet
import com.example.pawcare.domain.model.Owner
import com.example.pawcare.domain.repository.OwnerRepository
import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetRegisterViewModel @Inject constructor(
    private val ownerRepository: OwnerRepository,
    private val petRepository: PetRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PetRegisterState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PetRegisterEffect>()
    val effect = _effect.asSharedFlow()

    init {
        // Obtenemos el ID si venimos de la pantalla de Perfil (Modo Edición)
        val petId: String? = savedStateHandle["petId"]
        if (petId != null) {
            loadPetData(petId)
        }
    }

    private fun loadPetData(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(petId = id, isLoading = true) }

            val petResult = petRepository.getPetById(id)

            if (petResult is Resource.Success) {
                val pet = petResult.data ?: return@launch

                _state.update { it.copy(
                    petId = pet.id,
                    ownerId = pet.ownerId,
                    name = pet.name,
                    breed = pet.breed,
                    age = pet.age.toString()
                ) }

                val ownerResult = ownerRepository.getOwnerById(pet.ownerId)

                if (ownerResult is Resource.Success) {
                    val owner = ownerResult.data ?: return@launch

                    _state.update { it.copy(
                        isLoading = false,
                        ownerFullName = owner.fullName,
                        ownerPhone = owner.phone,
                        ownerEmail = owner.email,
                        ownerAddress = owner.address
                    ) }
                } else if (ownerResult is Resource.Error) {
                    _state.update { it.copy(isLoading = false, error = ownerResult.message) }
                }
            } else if (petResult is Resource.Error) {
                _state.update { it.copy(isLoading = false, error = petResult.message) }
            }
        }
    }

    fun onEvent(event: PetRegisterEvent) {
        when (event) {
            is PetRegisterEvent.OnNameChanged -> _state.update { it.copy(name = event.name) }
            is PetRegisterEvent.OnBreedChanged -> _state.update { it.copy(breed = event.breed) }
            is PetRegisterEvent.OnAgeChanged -> _state.update { it.copy(age = event.age) }
            is PetRegisterEvent.OnWeightChanged -> _state.update { it.copy(weight = event.weight) }
            is PetRegisterEvent.OnOwnerFullNameChanged -> _state.update { it.copy(ownerFullName = event.name) }
            is PetRegisterEvent.OnOwnerPhoneChanged -> _state.update { it.copy(ownerPhone = event.phone) }
            is PetRegisterEvent.OnOwnerEmailChanged -> _state.update { it.copy(ownerEmail = event.email) }
            is PetRegisterEvent.OnOwnerAddressChanged -> _state.update { it.copy(ownerAddress = event.address) }
            PetRegisterEvent.SavePet -> savePet()
            is PetRegisterEvent.OnDeletePet -> { /* No hace nada en esta pantalla */ }
        }
    }

    private fun savePet() {
        viewModelScope.launch {
            val currentState = _state.value
            val petIdFromNav: String? = savedStateHandle["petId"]

            val ageInt = currentState.age.toIntOrNull() ?: 0
            if (ageInt <= 0 || currentState.name.isBlank()) {
                _state.update { it.copy(error = "Por favor completa los datos correctamente") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            if (petIdFromNav != null) {
                val petResult = petRepository.getPetById(petIdFromNav)
                if (petResult is Resource.Success) {
                    val currentPet = petResult.data!!

                    val updateOwnerResult = ownerRepository.updateOwner(
                        id = currentPet.ownerId,
                        fullName = currentState.ownerFullName,
                        phone = currentState.ownerPhone,
                        email = currentState.ownerEmail,
                        address = currentState.ownerAddress,
                        isVip = false
                    )

                    if (updateOwnerResult is Resource.Success) {
                        val updatePetResult = petRepository.updatePet(
                            id = petIdFromNav,
                            name = currentState.name,
                            breed = currentState.breed,
                            age = ageInt,
                            photoUrl = null,
                            ownerId = currentPet.ownerId
                        )
                        handleResult(updatePetResult)
                    } else if (updateOwnerResult is Resource.Error) {
                        _state.update { it.copy(isLoading = false, error = updateOwnerResult.message) }
                    }
                }
            } else {
                // --- MODO CREACIÓN ---
                val ownerResult = ownerRepository.createOwner(
                    fullName = currentState.ownerFullName,
                    phone = currentState.ownerPhone,
                    email = currentState.ownerEmail,
                    address = currentState.ownerAddress,
                    isVip = false
                )

                if (ownerResult is Resource.Success) {
                    val petResult = petRepository.createPet(
                        name = currentState.name,
                        breed = currentState.breed,
                        age = ageInt,
                        photoUrl = null,
                        ownerId = ownerResult.data!!.id
                    )
                    handleResult(petResult)
                } else if (ownerResult is Resource.Error) { // CAMBIA EL 'else' POR ESTO
                    _state.update { it.copy(isLoading = false, error = ownerResult.message) }
                }
            }
        }
    }

    private suspend fun handleResult(result: Resource<Pet>) {
        when (result) {
            is Resource.Success -> {
                val id = result.data?.id ?: ""
                _state.update { it.copy(isLoading = false, isSaved = true, savedPetId = id) }
                _effect.emit(PetRegisterEffect.NavigateToConfirmation(id))
            }
            is Resource.Error -> {
                _state.update { it.copy(isLoading = false, error = result.message) }
            }
            else -> {}
        }
    }
}