package com.asurspace.criminalintent.domain.repository

import com.asurspace.criminalintent.model.crimes.entities.Crime

interface CreateCrimeRepository {

    suspend fun addCrime(crime: Crime)

}