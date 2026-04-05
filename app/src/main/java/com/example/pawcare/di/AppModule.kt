package com.example.pawcare.di

import android.content.Context
import androidx.room.Room
import com.example.pawcare.data.local.PawCareDatabase
import com.example.pawcare.data.local.dao.*
import com.example.pawcare.data.remote.PawCareApiService
import com.example.pawcare.data.repository.*
import com.example.pawcare.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PawCareDatabase {
        return Room.databaseBuilder(
            context,
            PawCareDatabase::class.java,
            "pawcare_db"
        ).build()
    }

    @Provides
    fun provideOwnerDao(db: PawCareDatabase) = db.ownerDao()

    @Provides
    fun providePetDao(db: PawCareDatabase) = db.petDao()

    @Provides
    fun provideServiceDao(db: PawCareDatabase) = db.serviceDao()

    @Provides
    fun provideAppointmentDao(db: PawCareDatabase) = db.appointmentDao()

    @Provides
    fun provideProductDao(db: PawCareDatabase) = db.productDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): PawCareApiService {
        return Retrofit.Builder()
            .baseUrl("https://pawwcareapi-fje8h9d4eabsekf2.canadacentral-01.azurewebsites.net/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PawCareApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOwnerRepository(
        api: PawCareApiService,
        ownerDao: OwnerDao,
        petDao: PetDao
    ): OwnerRepository = OwnerRepositoryImpl(api, ownerDao, petDao)

    @Provides
    @Singleton
    fun providePetRepository(
        api: PawCareApiService,
        petDao: PetDao
    ): PetRepository = PetRepositoryImpl(api, petDao)

    @Provides
    @Singleton
    fun provideServiceRepository(
        api: PawCareApiService,
        serviceDao: ServiceDao
    ): ServiceRepository = ServiceRepositoryImpl(api, serviceDao)

    @Provides
    @Singleton
    fun provideAppointmentRepository(
        api: PawCareApiService,
        appointmentDao: AppointmentDao
    ): AppointmentRepository = AppointmentRepositoryImpl(api, appointmentDao)

    @Provides
    @Singleton
    fun provideProductRepository(
        api: PawCareApiService,
        productDao: ProductDao
    ): ProductRepository = ProductRepositoryImpl(api, productDao)
}
