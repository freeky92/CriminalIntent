package com.asurspace.criminalintent.data.repository

import com.asurspace.criminalintent.data.local.dao.CreateCrimeDao
import com.asurspace.criminalintent.data.local.dao.GetCrimesDao
import com.asurspace.criminalintent.data.local.dao.SearchCrimesDao
import com.asurspace.criminalintent.data.local.dao.UpdateCrimeDao
import com.asurspace.criminalintent.domain.repository.GetAddUpdateCrimeRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAddUpdateCrimeRepositoryImpl @Inject constructor(
    private val createCrimeDao: CreateCrimeDao,
    private val getCrimesDao: GetCrimesDao,
    private val searchCrimesDao: SearchCrimesDao,
    private val updateCrimeDao: UpdateCrimeDao
): GetAddUpdateCrimeRepository {

    override fun getAllCrimes(onlyActive: Boolean?): Flow<MutableList<Crime>> = getCrimesDao.getAllCrimes()
        .map { it -> it.toList().map { it.toCrime() }.asReversed().toMutableList() }
        .flowOn(Dispatchers.IO)

    override fun findCrimeById(crimeId: Long): Flow<Crime> = searchCrimesDao.findCrimeById(crimeId)
        .map { entity -> entity.toCrime() }
        .flowOn(Dispatchers.IO)

    override suspend fun addCrime(crime: Crime) {
        withContext(Dispatchers.IO) {
            createCrimeDao.createCrime(crime.toCrimeDbEntity())
        }
    }

    override suspend fun setSolved(solvedTuples: SetSolvedTuples) {
        withContext(Dispatchers.IO) {
            updateCrimeDao.setSolved(solvedTuples)
        }
    }

    override suspend fun updateCrime(crime: Crime?) {
        withContext(Dispatchers.IO) {
            crime?.let { updateCrimeDao.updateCrime(it.toCrimeDbEntity()) }
        }
    }

}