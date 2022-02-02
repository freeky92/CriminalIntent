package com.asurspace.criminalintent.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asurspace.criminalintent.model.crimes.room.CrimesDao
import com.asurspace.criminalintent.model.crimes.room.entyties.CrimeDbEntity

@Database(
    version = 1,
    entities = [CrimeDbEntity::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCrimesDao(): CrimesDao

}