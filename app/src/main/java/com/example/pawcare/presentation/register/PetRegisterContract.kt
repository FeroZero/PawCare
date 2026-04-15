package com.example.pawcare.presentation.register

import com.example.pawcare.domain.model.Owner
import com.example.pawcare.presentation.components.pets.PetUiEvent

data class PetRegisterState(
    val petId: String? = null,
    val ownerId: String? = null,
    val name: String = "",
    val breed: String = "",
    val age: String = "",
    val weight: String = "",
    val ownerFullName: String = "",
    val ownerPhone: String = "",
    val ownerEmail: String = "",
    val ownerAddress: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val savedPetId: String? = null
)

sealed class PetRegisterEvent {
    data class OnNameChanged(val name: String) : PetRegisterEvent()
    data class OnBreedChanged(val breed: String) : PetRegisterEvent()
    data class OnAgeChanged(val age: String) : PetRegisterEvent()
    data class OnWeightChanged(val weight: String) : PetRegisterEvent()
    data class OnOwnerFullNameChanged(val name: String) : PetRegisterEvent()
    data class OnOwnerPhoneChanged(val phone: String) : PetRegisterEvent()
    data class OnOwnerEmailChanged(val email: String) : PetRegisterEvent()
    data class OnOwnerAddressChanged(val address: String) : PetRegisterEvent()

    data class OnDeletePet(val id: String) : PetRegisterEvent()
    object SavePet : PetRegisterEvent()
}

sealed class PetRegisterEffect {
    data class NavigateToConfirmation(val petId: String) : PetRegisterEffect()
    object NavigateBack : PetRegisterEffect()
}
