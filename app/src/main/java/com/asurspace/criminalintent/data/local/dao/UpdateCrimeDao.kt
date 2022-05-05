package com.asurspace.criminalintent.data.local.dao

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.asurspace.criminalintent.data.model.crimes.room.entyties.CrimeDbEntity
import com.asurspace.criminalintent.data.model.crimes.room.entyties.SetSolvedTuples

@Dao
interface UpdateCrimeDao {

    @Update(entity = CrimeDbEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCrime(crimeDbEntity: CrimeDbEntity)


    @Update(entity = CrimeDbEntity::class)
    suspend fun setSolved(setSolvedTuples: SetSolvedTuples)

}