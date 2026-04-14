package com.example.pawcare.presentation.components.owners

import com.example.pawcare.domain.model.Owner

data class OwnerUiState(
    val owners: List<Owner> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,

    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val isVip: Boolean = false,

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)