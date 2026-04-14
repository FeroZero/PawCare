package com.example.pawcare.domain.use_case.owners

import com.example.pawcare.domain.repository.OwnerRepository
import com.example.pawcare.domain.util.Resource
import javax.inject.Inject

class DeleteOwnerUseCase @Inject constructor(
    private val repository: OwnerRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        return repository.deleteOwner(id)
    }
}