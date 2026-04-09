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
import com.example.pawcare.domain.util.onFailureSuspend
import com.example.pawcare.domain.util.onSuccessSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class OwnerRepositoryImpl @Inject constructor(
    private val api: PawCareApiService,
    private val ownerDao: OwnerDao,
    private val petDao: PetDao
) : OwnerRepository, SafeApiCall {

    override fun getOwners(): Flow<Resource<List<Owner>>> = flow {
        emit(Resource.Loading())

        val result = safeApiCall { api.searchPets("") }

        result.onSuccessSuspend { petDtos ->
            val ownerDtos = petDtos.mapNotNull { it.owner }.distinctBy { it.id }
            ownerDao.upsertOwners(ownerDtos.map { it.toEntity() })
        }.onFailureSuspend { errorMessage ->
            emit(Resource.Error(errorMessage))
        }

        emitAll(
            ownerDao.getAllOwners().map { entities ->
                Resource.Success(entities.map { it.toOwner() })
            }
        )
    }.flowOn(Dispatchers.IO)

    override fun getOwnerById(id: String): Flow<Resource<Owner>> = flow {
        emit(Resource.Loading())

        val result = safeApiCall { api.getOwnerById(id) }

        result.onSuccessSuspend { dto ->
            ownerDao.upsertOwners(listOf(dto.toEntity()))
        }.onFailureSuspend { errorMessage ->
            emit(Resource.Error(errorMessage))
        }

        emitAll(
            ownerDao.getOwnerById(id).map { entity ->
                if (entity != null) {
                    Resource.Success(entity.toOwner())
                } else {
                    Resource.Error("Dueño no encontrado")
                }
            }
        )
    }.flowOn(Dispatchers.IO)

    override fun getOwnerPets(ownerId: String): Flow<Resource<List<Pet>>> = flow {
        emit(Resource.Loading())

        val result = safeApiCall { api.getOwnerPets(ownerId) }

        result.onSuccessSuspend { dtos ->
            petDao.upsertPets(dtos.map { it.toEntity() })
        }.onFailureSuspend { errorMessage ->
            emit(Resource.Error(errorMessage))
        }

        emitAll(
            petDao.getAllPets().map { entities ->
                val filtered = entities.filter { it.ownerId == ownerId }.map { it.toPet() }
                Resource.Success(filtered)
            }
        )
    }.flowOn(Dispatchers.IO)

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
