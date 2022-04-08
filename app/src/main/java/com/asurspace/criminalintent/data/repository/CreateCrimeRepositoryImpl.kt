package com.asurspace.criminalintent.data.repository

import com.asurspace.criminalintent.data.local.dao.CreateCrimeDao
import com.asurspace.criminalintent.domain.repository.CreateCrimeRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateCrimeRepositoryImpl @Inject constructor(
    private val createCrimeDao: CreateCrimeDao
): CreateCrimeRepository {

    override suspend fun addCrime(crime: Crime) {
        withContext(Dispatchers.IO) {
            createCrimeDao.createCrime(crime.toCrimeDbEntity())
        }
    }

}