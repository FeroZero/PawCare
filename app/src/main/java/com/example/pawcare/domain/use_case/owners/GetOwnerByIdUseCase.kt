package com.example.pawcare.domain.use_case.owners

import com.example.pawcare.domain.model.Owner
import com.example.pawcare.domain.repository.OwnerRepository
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOwnerByIdUseCase @Inject constructor(
    private val repository: OwnerRepository
) {
    suspend operator fun invoke(id: String): Resource<Owner> = repository.getOwnerById(id)
}