package com.asurspace.criminalintent.domain.repository

import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import kotlinx.coroutines.flow.Flow

interface CrimesListRepository {

    fun getAllCrimes(onlyActive: Boolean?): Flow<MutableList<Crime>>

    suspend fun setSolved(solvedTuples: SetSolvedTuples)

    suspend fun deleteCrime(crimeId: Long): Int

    suspend fun clearCrimes()

}