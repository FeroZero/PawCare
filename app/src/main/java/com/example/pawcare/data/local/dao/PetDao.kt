package com.example.pawcare.data.local.dao

import androidx.room.*
import com.example.pawcare.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE id = :id")
    fun getPetById(id: String): Flow<PetEntity?>

    @Query("SELECT * FROM pets WHERE name LIKE '%' || :query || '%' OR breed LIKE '%' || :query || '%'")
    fun searchPets(query: String): Flow<List<PetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPets(pets: List<PetEntity>)
}
