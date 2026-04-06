package com.example.pawcare.domain.use_case.services

import com.example.pawcare.domain.model.Service
import com.example.pawcare.domain.repository.ServiceRepository
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServicesUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    operator fun invoke(): Flow<Resource<List<Service>>> = repository.getServices()
}