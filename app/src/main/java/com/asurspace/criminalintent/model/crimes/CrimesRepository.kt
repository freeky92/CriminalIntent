package com.asurspace.criminalintent.model.crimes

import com.asurspace.criminalintent.model.crimes.entities.Crime
import kotlinx.coroutines.flow.*


interface CrimesRepository {

    suspend fun getAllCrimes(onlyActive: Boolean?): List<Crime>?

    suspend fun getCrimeByIdF(crimeId: Long): Crime?

    suspend fun addCrime(crime: Crime)

    suspend fun updateCrime(id: String, pair: Pair<String, Any>)

    suspend fun deleteCrime(id: String?): Int?

    suspend fun deleteAllCrimes()

}