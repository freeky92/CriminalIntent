package com.asurspace.criminalintent.model.crimes.room

import androidx.room.*
import com.asurspace.criminalintent.model.crimes.room.entyties.CrimeDbEntity
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import kotlinx.coroutines.flow.Flow

@Dao
interface CrimesDao {

    @Query("SELECT * FROM crimes")
    fun getAllCrimes(): Flow<List<CrimeDbEntity>>

    @Query("SELECT * FROM crimes WHERE id = :id")
    suspend fun findCrimeById(id: Long): CrimeDbEntity?

    @Insert
    suspend fun addCrime(crimeDbEntity: CrimeDbEntity)

    @Update(entity = CrimeDbEntity::class)
    suspend fun setSolved(setSolvedTuples: SetSolvedTuples)

    @Update(entity = CrimeDbEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCrime(crimeDbEntity: CrimeDbEntity)

    @Query("DELETE FROM crimes WHERE id =:id")
    suspend fun deleteCrime(id: Long): Int

    @Query("DELETE FROM crimes")
    suspend fun clearTable()

}