package com.example.pawcare.domain.use_case.pets

import com.example.pawcare.domain.model.Pet
import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPetByIdUseCase @Inject constructor(
    private val repository: PetRepository
) {
    suspend operator fun invoke(id: String): Resource<Pet> = repository.getPetById(id)
}