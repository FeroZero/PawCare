package com.example.pawcare.domain.repository

import com.example.pawcare.domain.model.Owner
import com.example.pawcare.domain.model.Pet
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface OwnerRepository {
    fun getOwners(): Flow<Resource<List<Owner>>>
    fun getOwnerById(id: String): Flow<Resource<Owner>>
    fun getOwnerPets(ownerId: String): Flow<Resource<List<Pet>>>
    suspend fun createOwner(fullName: String, phone: String, email: String, address: String, isVip: Boolean): Resource<Owner>
}
