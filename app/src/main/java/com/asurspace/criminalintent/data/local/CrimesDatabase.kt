package com.asurspace.criminalintent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asurspace.criminalintent.data.local.dao.*
import com.asurspace.criminalintent.data.model.crimes.room.entyties.CrimeDbEntity

@Database(
    version = 1,
    entities = [CrimeDbEntity::class]
)
//@TypeConverters(Converters::class)
abstract class CrimesDatabase : RoomDatabase() {
    abstract fun getCreateCrimeDao(): CreateCrimeDao
    abstract fun getGetCrimesListDao(): GetCrimesDao
    abstract fun getRemoveCrimeDao(): RemoveCrimeDao
    abstract fun getSearchCrimesDao(): SearchCrimesDao
    abstract fun getUpdateCrimeDao(): UpdateCrimeDao
}