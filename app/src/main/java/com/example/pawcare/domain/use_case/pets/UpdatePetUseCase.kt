package com.example.pawcare.domain.use_case.pets

import com.example.pawcare.domain.model.Pet
import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.domain.util.Resource
import javax.inject.Inject

class UpdatePetUseCase @Inject constructor(
    private val repository: PetRepository
) {
    suspend operator fun invoke(
        id: String,
        name: String,
        breed: String,
        age: Int,
        photoUrl: String?,
        ownerId: String
    ): Resource<Pet> = repository.updatePet(id, name, breed, age, photoUrl, ownerId)
}