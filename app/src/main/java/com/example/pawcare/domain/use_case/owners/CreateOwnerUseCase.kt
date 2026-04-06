package com.example.pawcare.domain.use_case.owners

import com.example.pawcare.domain.model.Owner
import com.example.pawcare.domain.repository.OwnerRepository
import com.example.pawcare.domain.util.Resource
import javax.inject.Inject

class CreateOwnerUseCase @Inject constructor(
    private val repository: OwnerRepository
) {
    suspend operator fun invoke(
        fullName: String,
        phone: String,
        email: String,
        address: String,
        isVip: Boolean
    ): Resource<Owner> = repository.createOwner(fullName, phone, email, address, isVip)
}