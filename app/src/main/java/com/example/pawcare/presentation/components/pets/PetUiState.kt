package com.example.pawcare.presentation.components.pets

import com.example.pawcare.domain.model.Pet

data class PetUiState(
    val pets: List<Pet> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)