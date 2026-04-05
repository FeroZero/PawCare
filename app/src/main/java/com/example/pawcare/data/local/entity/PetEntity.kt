package com.example.pawcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val breed: String,
    val age: Int,
    val photoUrl: String?,
    val ownerId: String,
    val ownerName: String,
    val createdAt: String
)
