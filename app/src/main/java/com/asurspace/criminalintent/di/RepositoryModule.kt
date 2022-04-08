package com.asurspace.criminalintent.di

import android.app.Application
import androidx.room.Room
import com.asurspace.criminalintent.common.utils.CrimesTable.DB_NAME
import com.asurspace.criminalintent.data.local.CrimesDatabase
import com.asurspace.criminalintent.data.local.dao.*
import com.asurspace.criminalintent.data.repository.CrimesListRepositoryImpl
import com.asurspace.criminalintent.domain.repository.CrimesListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providesAppDataBase(application: Application): CrimesDatabase {
        return Room.databaseBuilder(application, CrimesDatabase::class.java, DB_NAME)
            //.createFromAsset("init_database.db")
            //.fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideCrimesListRepository(crimesListDao: GetCrimesDao): CrimesListRepository {
        return CrimesListRepositoryImpl(crimesListDao)
    }

    @Singleton
    @Provides
    fun providesCrimesListDao(appDatabase: CrimesDatabase): GetCrimesDao {
        return appDatabase.getCrimesListDao()
    }

    @Singleton
    @Provides
    fun providesUpdateCrimesDao(appDatabase: CrimesDatabase): UpdateCrimeDao {
        return appDatabase.getUpdateCrimeDao()
    }

    @Singleton
    @Provides
    fun providesCreateCrimesDao(appDatabase: CrimesDatabase): CreateCrimeDao {
        return appDatabase.getCreateCrimeDao()
    }

    @Singleton
    @Provides
    fun providesSearchCrimesDao(appDatabase: CrimesDatabase): SearchCrimesDao {
        return appDatabase.getSearchCrimesDao()
    }

    @Singleton
    @Provides
    fun providesRemoveCrimesDao(appDatabase: CrimesDatabase): RemoveCrimeDao {
        return appDatabase.getRemoveCrimeDao()
    }

}