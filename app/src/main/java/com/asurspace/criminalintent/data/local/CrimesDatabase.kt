package com.asurspace.criminalintent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asurspace.criminalintent.data.local.dao.*
import com.asurspace.criminalintent.model.crimes.room.entyties.CrimeDbEntity

@Database(
    version = 1,
    entities = [CrimeDbEntity::class]
)
//@TypeConverters(Converters::class)
abstract class CrimesDatabase : RoomDatabase() {
    abstract fun getSearchCrimesDao(): SearchCrimesDao
    abstract fun getCreateCrimeDao(): CreateCrimeDao
    abstract fun getUpdateCrimeDao(): UpdateCrimeDao
    abstract fun getRemoveCrimeDao(): RemoveCrimeDao
    abstract fun getCrimesListDao(): GetCrimesDao
}