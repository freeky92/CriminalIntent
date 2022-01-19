package com.asurspace.criminalintent.model.crimes

import com.asurspace.criminalintent.model.crimes.entities.Crime
import kotlinx.coroutines.flow.*


interface CrimesRepository {

    suspend fun getAllCrimes(onlyActive: Boolean?): List<Crime>?

    suspend fun getCrimeByIdVMS(crimeId: Long): Crime?

    suspend fun addCrime(crime: Crime)

    suspend fun updateCrime(crimeId: Long, pair: Pair<String, Any>)

    suspend fun deleteCrime(crimeId: Long): Int?

    suspend fun deleteAllCrimes()

}