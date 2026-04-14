package com.example.pawcare.domain.use_case.services

import com.example.pawcare.domain.repository.ServiceRepository
import com.example.pawcare.domain.util.Resource
import javax.inject.Inject

class DeleteServicesUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        return repository.deleteServices(id)
    }
}