package com.asurspace.criminalintent.domain.repository

interface RemoveCrimesRepository {

    suspend fun deleteCrime(crimeId: Long): Int

    suspend fun clearCrimes()

}