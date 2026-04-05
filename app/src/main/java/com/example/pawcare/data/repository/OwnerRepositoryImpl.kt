package com.example.pawcare.data.repository

import com.example.pawcare.data.local.dao.OwnerDao
import com.example.pawcare.data.local.dao.PetDao
import com.example.pawcare.data.mapper.toEntity
import com.example.pawcare.data.mapper.toOwner
import com.example.pawcare.data.mapper.toPet
import com.example.pawcare.data.remote.PawCareApiService
import com.example.pawcare.data.remote.SafeApiCall
import com.example.pawcare.data.remote.dto.CreateOwnerRequest
import com.example.pawcare.domain.model.Owner
import com.example.pawcare.domain.model.Pet
import com.example.pawcare.domain.repository.OwnerRepository
import com.example.pawcare.domain.util.Resource
import com.example.pawcare.domain.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OwnerRepositoryImpl @Inject constructor(
    private val api: PawCareApiService,
    private val ownerDao: OwnerDao,
    private val petDao: PetDao
) : OwnerRepository, SafeApiCall {

    override fun getOwners(): Flow<Resource<List<Owner>>> = networkBoundResource(
        query = { ownerDao.getAllOwners().map { entities -> entities.map { it.toOwner() } } },
        fetch = { api.searchPets("").body()?.mapNotNull { it.owner }?.distinctBy { it.id } ?: emptyList() },
        saveFetchResult = { dtos ->
            ownerDao.upsertOwners(dtos.map { it.toEntity() })
        }
    )

    override fun getOwnerById(id: String): Flow<Resource<Owner>> = networkBoundResource(
        query = { ownerDao.getOwnerById(id).map { it?.toOwner() ?: Owner("", "", "", "", "", false, "") } },
        fetch = { api.getOwnerById(id).body()!! },
        saveFetchResult = { dto ->
            ownerDao.upsertOwners(listOf(dto.toEntity()))
        }
    )

    override fun getOwnerPets(ownerId: String): Flow<Resource<List<Pet>>> = networkBoundResource(
        query = { petDao.getAllPets().map { pets -> pets.filter { it.ownerId == ownerId }.map { it.toPet() } } },
        fetch = { api.getOwnerPets(ownerId).body() ?: emptyList() },
        saveFetchResult = { dtos ->
            petDao.upsertPets(dtos.map { it.toEntity() })
        }
    )

    override suspend fun createOwner(
        fullName: String,
        phone: String,
        email: String,
        address: String,
        isVip: Boolean
    ): Resource<Owner> {
        val result = safeApiCall {
            api.createOwner(CreateOwnerRequest(fullName, phone, email, address, isVip))
        }
        return when (result) {
            is Resource.Success -> {
                ownerDao.upsertOwners(listOf(result.data.toEntity()))
                Resource.Success(result.data.toOwner())
            }
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading()
        }
    }
}
