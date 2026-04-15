package com.example.pawcare.domain.use_case.owners

import com.example.pawcare.domain.model.Owner
import com.example.pawcare.domain.repository.OwnerRepository
import com.example.pawcare.domain.util.Resource
import javax.inject.Inject

class UpdateOwnerUseCase @Inject constructor(
    private val repository: OwnerRepository
) {
    suspend operator fun invoke(
        id: String,
        fullName: String,
        phone: String,
        email: String,
        address: String,
        isVip: Boolean
    ): Resource<Owner> {
        return repository.updateOwner(
            id = id,
            fullName = fullName,
            phone = phone,
            email = email,
            address = address,
            isVip = isVip
        )
    }
}