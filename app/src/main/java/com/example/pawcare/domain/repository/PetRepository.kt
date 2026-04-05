package com.example.pawcare.domain.repository

import com.example.pawcare.domain.model.Pet
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun getPets(): Flow<Resource<List<Pet>>>
    fun getPetById(id: String): Flow<Resource<Pet>>
    fun searchPets(query: String): Flow<Resource<List<Pet>>>
    suspend fun createPet(name: String, breed: String, age: Int, photoUrl: String?, ownerId: String): Resource<Pet>
    suspend fun updatePet(id: String, name: String, breed: String, age: Int, photoUrl: String?, ownerId: String): Resource<Pet>
}
