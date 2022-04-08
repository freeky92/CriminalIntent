package com.asurspace.criminalintent.domain.repository

import com.asurspace.criminalintent.model.crimes.entities.Crime

interface EditCrimeRepository {

    suspend fun updateCrime(crime: Crime?)

    suspend fun deleteCrime(crimeId: Long): Int

}