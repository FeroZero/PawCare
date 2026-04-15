package com.example.pawcare.presentation.components.owners

sealed interface OwnerUiEvent {
    object Refresh : OwnerUiEvent

    data class OnSearchQueryChange(val query: String) : OwnerUiEvent

    data class OnOwnerClick(val ownerId: String) : OwnerUiEvent

    data class OnFullNameChange(val value: String) : OwnerUiEvent
    data class OnPhoneChange(val value: String) : OwnerUiEvent
    data class OnEmailChange(val value: String) : OwnerUiEvent
    data class OnAddressChange(val value: String) : OwnerUiEvent
    data class OnVipStatusChange(val isVip: Boolean) : OwnerUiEvent

    data class OnDeleteOwner(val ownerId: String) : OwnerUiEvent
    object OnSaveOwnerClick : OwnerUiEvent
}