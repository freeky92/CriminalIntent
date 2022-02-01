package com.asurspace.criminalintent.model.crimes

import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import kotlinx.coroutines.flow.*


interface CrimesRepository {

    suspend fun getAllCrimes(onlyActive: Boolean?): List<Crime>?

    suspend fun findCrimeById(crimeId: Long): Crime?

    suspend fun addCrime(crime: Crime)

    suspend fun setSolved(solvedTuples: SetSolvedTuples)

    suspend fun updateCrime(crime: Crime?)

    suspend fun deleteCrime(crimeId: Long): Int

    suspend fun clearCrimes()

}