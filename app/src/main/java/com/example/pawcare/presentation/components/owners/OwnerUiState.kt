package com.example.pawcare.presentation.components.owners

import com.example.pawcare.domain.model.Owner

data class OwnerUiState(
    val owners: List<Owner> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)