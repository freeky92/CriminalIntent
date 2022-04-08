package com.asurspace.criminalintent.data.repository

import com.asurspace.criminalintent.data.local.dao.UpdateCrimeDao
import com.asurspace.criminalintent.domain.repository.EditCrimeRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditCrimeRepositoryImpl @Inject constructor(
    private val editCreateCrimeDao: UpdateCrimeDao
): EditCrimeRepository {

    override suspend fun updateCrime(crime: Crime?) {
        withContext(Dispatchers.IO) {
            crime?.let { editCreateCrimeDao.updateCrime(it.toCrimeDbEntity()) }
        }
    }

    override suspend fun deleteCrime(crimeId: Long): Int {
        return withContext(Dispatchers.IO) {
            return@withContext (crimeId)
        }
    }

}