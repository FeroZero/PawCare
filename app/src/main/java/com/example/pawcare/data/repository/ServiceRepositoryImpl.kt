package com.example.pawcare.data.repository

import com.example.pawcare.data.local.dao.ServiceDao
import com.example.pawcare.data.mapper.toEntity
import com.example.pawcare.data.mapper.toService
import com.example.pawcare.data.remote.PawCareApiService
import com.example.pawcare.domain.model.Service
import com.example.pawcare.domain.repository.ServiceRepository
import com.example.pawcare.domain.util.Resource
import com.example.pawcare.domain.util.networkBoundResource
import com.example.pawcare.data.remote.SafeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val api: PawCareApiService,
    private val serviceDao: ServiceDao
) : ServiceRepository, SafeApiCall {

    override fun getServices(): Flow<Resource<List<Service>>> = networkBoundResource(
        query = { serviceDao.getAllServices().map { entities -> entities.map { it.toService() } } },
        fetch = { api.getServices().body() ?: emptyList() },
        saveFetchResult = { dtos ->
            serviceDao.upsertServices(dtos.map { it.toEntity() })
        }
    )

    override suspend fun deleteServices(id: String): Resource<Unit> {
        // Especificamos <Unit> explícitamente para resolver el error de inferencia
        val result = safeApiCall<Unit> { api.deleteService(id) }

        return when (result) {
            is Resource.Success -> {
                serviceDao.deleteServices(id)
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error<Unit>(result.message)
            }
            is Resource.Loading -> {
                Resource.Loading<Unit>()
            }
        }
    }
}
