package com.asurspace.criminalintent.model.crimes

import com.asurspace.criminalintent.model.crimes.entities.Crime
import kotlinx.coroutines.flow.*


interface CrimesRepository {

    suspend fun getAllCrimes(onlyActive: Boolean?): List<Crime>?

    suspend fun findCrimeByIdVMS(crimeId: Long): Crime?

    suspend fun addCrime(crime: Crime?)

    suspend fun updateCrime(crimeId: Long?, crime: Crime?)

    suspend fun deleteCrime(crimeId: Long?): Int?

    suspend fun clearCrimes()

}