package com.asurspace.criminalintent.data.repository

import com.asurspace.criminalintent.data.local.dao.GetCrimesDao
import com.asurspace.criminalintent.domain.repository.CrimesListRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CrimesListRepositoryImpl @Inject constructor(
    private val crimesListDao: GetCrimesDao
) : CrimesListRepository {

    override fun getAllCrimes(onlyActive: Boolean?): Flow<MutableList<Crime>> = crimesListDao.getAllCrimes()
        .map { it -> it.toList().map { it.toCrime() }.asReversed().toMutableList() }
        .flowOn(Dispatchers.IO)

    override suspend fun setSolved(solvedTuples: SetSolvedTuples) {
        withContext(Dispatchers.IO) {
            crimesListDao.setSolved(solvedTuples)
        }
    }

    override suspend fun deleteCrime(crimeId: Long): Int {
        return withContext(Dispatchers.IO) {
            return@withContext crimesListDao.deleteCrime(crimeId)
        }
    }

    override suspend fun clearCrimes() {
        return withContext(Dispatchers.IO) {
            return@withContext crimesListDao.clearTable()
        }
    }

}