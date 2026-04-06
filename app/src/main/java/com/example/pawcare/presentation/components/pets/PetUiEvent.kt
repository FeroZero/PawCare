package com.example.pawcare.presentation.components.pets

sealed interface PetUiEvent {
    data class OnSearchQueryChange(val query: String) : PetUiEvent
    object Refresh : PetUiEvent
}