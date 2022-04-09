package com.asurspace.criminalintent.data.repository

import com.asurspace.criminalintent.data.local.dao.GetCrimesDao
import com.asurspace.criminalintent.data.local.dao.RemoveCrimeDao
import com.asurspace.criminalintent.domain.repository.RemoveCrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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