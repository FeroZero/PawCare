package com.example.pawcare.presentation.components.pets

sealed interface PetUiEvent {
    object Refresh : PetUiEvent
    data class OnSearchQueryChange(val query: String) : PetUiEvent
    data class OnPetClick(val petId: String) : PetUiEvent

    data class OnNameChange(val value: String) : PetUiEvent
    data class OnBreedChange(val value: String) : PetUiEvent
    data class OnAgeChange(val value: String) : PetUiEvent
    data class OnOwnerIdChange(val value: String) : PetUiEvent
    data class OnPhotoUrlChange(val value: String?) : PetUiEvent

    object OnSavePetClick : PetUiEvent
    data class OnDeletePet(val id: String) : PetUiEvent

    data class OnUpdatePetClick(val id: String) : PetUiEvent
}