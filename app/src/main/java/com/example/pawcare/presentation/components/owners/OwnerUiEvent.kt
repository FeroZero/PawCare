package com.example.pawcare.presentation.components.owners

sealed interface OwnerUiEvent {
    object Refresh : OwnerUiEvent
}