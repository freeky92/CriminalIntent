package com.asurspace.criminalintent.data.local.dao

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.asurspace.criminalintent.common.utils.CrimesTable
import com.asurspace.criminalintent.common.utils.CrimesTable.TABLE_NAME
import com.asurspace.criminalintent.model.crimes.room.entyties.CrimeDbEntity
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples

@Dao
interface UpdateCrimeDao {

    @Update(entity = CrimeDbEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCrime(crimeDbEntity: CrimeDbEntity)


    @Update(entity = CrimeDbEntity::class)
    suspend fun setSolved(setSolvedTuples: SetSolvedTuples)

}