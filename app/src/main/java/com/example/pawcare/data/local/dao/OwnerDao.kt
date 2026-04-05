package com.example.pawcare.data.local.dao

import androidx.room.*
import com.example.pawcare.data.local.entity.OwnerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnerDao {
    @Query("SELECT * FROM owners")
    fun getAllOwners(): Flow<List<OwnerEntity>>

    @Query("SELECT * FROM owners WHERE id = :id")
    fun getOwnerById(id: String): Flow<OwnerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOwners(owners: List<OwnerEntity>)

    @Delete
    suspend fun deleteOwner(owner: OwnerEntity)
}
