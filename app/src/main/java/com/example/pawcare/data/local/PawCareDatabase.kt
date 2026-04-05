package com.example.pawcare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pawcare.data.local.dao.*
import com.example.pawcare.data.local.entity.*

@Database(
    entities = [
        OwnerEntity::class,
        PetEntity::class,
        ServiceEntity::class,
        AppointmentEntity::class,
        ProductEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PawCareDatabase : RoomDatabase() {
    abstract fun ownerDao(): OwnerDao
    abstract fun petDao(): PetDao
    abstract fun serviceDao(): ServiceDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun productDao(): ProductDao
}
