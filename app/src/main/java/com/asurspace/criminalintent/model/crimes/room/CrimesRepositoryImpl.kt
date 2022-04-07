package com.asurspace.criminalintent.model.crimes.room

import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CrimesRepositoryImpl @Inject constructor(
    private val crimesDao: CrimesDao
) : CrimesRepository {

    override fun getAllCrimes(onlyActive: Boolean?): Flow<MutableList<Crime>> =
        crimesDao.getAllCrimes()
            .map { it -> it.toList().map { it.toCrime() }.asReversed().toMutableList() }
            .flowOn(Dispatchers.IO)


    override suspend fun findCrimeById(crimeId: Long): Crime? =
        crimesDao.findCrimeById(crimeId)?.toCrime()


    override suspend fun addCrime(crime: Crime) {
        withContext(Dispatchers.IO) {
            crimesDao.addCrime(crime.toCrimeDbEntity())
        }
    }

    override suspend fun setSolved(solvedTuples: SetSolvedTuples) {
        withContext(Dispatchers.IO) {
            crimesDao.setSolved(solvedTuples)
        }
    }

    override suspend fun updateCrime(crime: Crime?) {
        withContext(Dispatchers.IO) {
            crime?.let { crimesDao.updateCrime(it.toCrimeDbEntity()) }
        }
    }

    override suspend fun deleteCrime(crimeId: Long): Int {
        return withContext(Dispatchers.IO) {
            return@withContext crimesDao.deleteCrime(crimeId)
        }
    }

    override suspend fun clearCrimes() {
        return withContext(Dispatchers.IO) {
            return@withContext crimesDao.clearTable()
        }
    }
}