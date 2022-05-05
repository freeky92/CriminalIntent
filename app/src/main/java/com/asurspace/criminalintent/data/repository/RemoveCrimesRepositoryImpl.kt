package com.asurspace.criminalintent.data.repository

import com.asurspace.criminalintent.data.local.dao.RemoveCrimeDao
import com.asurspace.criminalintent.domain.repository.RemoveCrimesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveCrimesRepositoryImpl @Inject constructor(
    private val removeCrimeDao: RemoveCrimeDao
) : RemoveCrimesRepository {

    override suspend fun deleteCrime(crimeId: Long): Int {
        return withContext(Dispatchers.IO) {
            return@withContext removeCrimeDao.deleteCrime(crimeId)
        }
    }

    override suspend fun clearCrimes() {
        return withContext(Dispatchers.IO) {
            return@withContext removeCrimeDao.clearTable()
        }
    }

}