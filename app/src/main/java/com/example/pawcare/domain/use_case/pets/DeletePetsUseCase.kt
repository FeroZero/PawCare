package com.example.pawcare.domain.use_case.pets

import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.domain.util.Resource
import javax.inject.Inject

class DeletePetsUseCase @Inject constructor(
    private val repository: PetRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        return repository.deletePet(id)
    }
}