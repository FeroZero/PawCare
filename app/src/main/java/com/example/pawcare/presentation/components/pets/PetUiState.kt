package com.example.pawcare.presentation.components.pets

import com.example.pawcare.domain.model.Pet

data class PetUiState(
    val pets: List<Pet> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,

    val totalRegistered: Int = 0,
    val appointmentsToday: Int = 0,
    val newPetsCount: Int = 0,

    val name: String = "",
    val breed: String = "",
    val age: String = "",
    val ownerId: String = "",
    val photoUrl: String? = null,

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)