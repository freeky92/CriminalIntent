package com.asurspace.criminalintent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.asurspace.criminalintent.data.model.crimes.room.entyties.CrimeDbEntity

@Dao
interface CreateCrimeDao {

    @Insert
    suspend fun createCrime(crimeDbEntity: CrimeDbEntity)

}