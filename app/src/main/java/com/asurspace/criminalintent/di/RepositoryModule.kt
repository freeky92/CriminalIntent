package com.asurspace.criminalintent.di

import android.app.Application
import androidx.room.Room
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.room.AppDatabase
import com.asurspace.criminalintent.model.crimes.room.CrimesDao
import com.asurspace.criminalintent.model.crimes.room.CrimesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideCrimeRepository(crimesDao: CrimesDao): CrimesRepository {
        return CrimesRepositoryImpl(crimesDao)
    }

    @Singleton
    @Provides
    fun providesCrimesDao(appDatabase: AppDatabase): CrimesDao {
        return appDatabase.getCrimesDao()
    }

    @Singleton
    @Provides
    fun providesAppDataBase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "database.db")
            //.createFromAsset("init_database.db")
            //.fallbackToDestructiveMigration()
            .build()
    }


}