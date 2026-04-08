package com.example.pawcare.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val petRepository: PetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PetRegisterState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PetRegisterEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: PetRegisterEvent) {
        when (event) {
            is PetRegisterEvent.OnNameChanged -> _state.update { it.copy(name = event.name) }
            is PetRegisterEvent.OnBreedChanged -> _state.update { it.copy(breed = event.breed) }
            is PetRegisterEvent.OnAgeChanged -> _state.update { it.copy(age = event.age) }
            is PetRegisterEvent.OnWeightChanged -> _state.update { it.copy(weight = event.weight) }
            is PetRegisterEvent.OnOwnerFullNameChanged -> _state.update { it.copy(ownerFullName = event.name) }
            is PetRegisterEvent.OnOwnerPhoneChanged -> _state.update { it.copy(ownerPhone = event.phone) }
            is PetRegisterEvent.OnOwnerEmailChanged -> _state.update { it.copy(ownerEmail = event.email) }
            PetRegisterEvent.SavePet -> savePet()
        }
    }

    private fun savePet() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // 1. Create Owner first
            val ownerResult = ownerRepository.createOwner(
                fullName = _state.value.ownerFullName,
                phone = _state.value.ownerPhone,
                email = _state.value.ownerEmail,
                address = "", // Design doesn't show address, using empty
                isVip = false
            )

            when (ownerResult) {
                is Resource.Success -> {
                    // 2. Create Pet with the new ownerId
                    val petResult = petRepository.createPet(
                        name = _state.value.name,
                        breed = _state.value.breed,
                        age = _state.value.age.toIntOrNull() ?: 0,
                        photoUrl = null,
                        ownerId = ownerResult.data.id
                    )

                    when (petResult) {
                        is Resource.Success -> {
                            _state.update { it.copy(isLoading = false, isSaved = true, savedPetId = petResult.data.id) }
                            _effect.emit(PetRegisterEffect.NavigateToConfirmation(petResult.data.id))
                        }
                        is Resource.Error -> {
                            _state.update { it.copy(isLoading = false, error = petResult.message) }
                        }
                        is Resource.Loading -> {}
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = ownerResult.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
