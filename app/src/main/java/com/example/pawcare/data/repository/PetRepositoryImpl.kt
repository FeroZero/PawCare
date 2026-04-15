package com.example.pawcare.data.repository

import com.example.pawcare.data.local.dao.PetDao
import com.example.pawcare.data.mapper.toEntity
import com.example.pawcare.data.mapper.toPet
import com.example.pawcare.data.remote.PawCareApiService
import com.example.pawcare.data.remote.SafeApiCall
import com.example.pawcare.data.remote.dto.CreatePetRequest
import com.example.pawcare.domain.model.Pet
import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.domain.util.Resource
import com.example.pawcare.domain.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PetRepositoryImpl @Inject constructor(
    private val api: PawCareApiService,
    private val petDao: PetDao
) : PetRepository, SafeApiCall {

    override fun getPets(): Flow<Resource<List<Pet>>> = networkBoundResource(
        query = { petDao.getAllPets().map { entities -> entities.map { it.toPet() } } },
        fetch = { api.searchPets("").body() ?: emptyList() },
        saveFetchResult = { dtos ->
            petDao.upsertPets(dtos.map { it.toEntity() })
        }
    )

    override suspend fun getPetById(id: String): Resource<Pet> {
        val result = safeApiCall { api.getPetById(id) }

        if (result is Resource.Success) {
            petDao.upsertPets(listOf(result.data.toEntity()))
        }

        val localPet = petDao.getPetById(id).firstOrNull()

        return if (localPet != null) {
            Resource.Success(localPet.toPet())
        } else {
            when (result) {
                is Resource.Error -> Resource.Error(result.message)
                else -> Resource.Error("Mascota no encontrada")
            }
        }
    }

    override fun searchPets(query: String): Flow<Resource<List<Pet>>> = networkBoundResource(
        query = { petDao.searchPets(query).map { entities -> entities.map { it.toPet() } } },
        fetch = { api.searchPets(query).body() ?: emptyList() },
        saveFetchResult = { dtos ->
            petDao.upsertPets(dtos.map { it.toEntity() })
        }
    )

    override suspend fun createPet(
        name: String,
        breed: String,
        age: Int,
        photoUrl: String?,
        ownerId: String
    ): Resource<Pet> {
        val result = safeApiCall {
            api.createPet(CreatePetRequest(name, breed, age, photoUrl, ownerId))
        }
        return when (result) {
            is Resource.Success -> {
                petDao.upsertPets(listOf(result.data.toEntity()))
                Resource.Success(result.data.toPet())
            }
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun updatePet(
        id: String,
        name: String,
        breed: String,
        age: Int,
        photoUrl: String?,
        ownerId: String
    ): Resource<Pet> {
        val result = safeApiCall {
            api.updatePet(id, CreatePetRequest(name, breed, age, photoUrl, ownerId))
        }
        return when (result) {
            is Resource.Success -> {
                petDao.upsertPets(listOf(result.data.toEntity()))
                Resource.Success(result.data.toPet())
            }
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun deletePet(id: String): Resource<Unit> {
        val result = safeApiCall { api.deletePet(id) }

        if (result is Resource.Success || result is Resource.Error) {
            petDao.deletePets(id)
        }
        return Resource.Success(Unit)
    }
}

