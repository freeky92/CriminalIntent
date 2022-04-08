package com.asurspace.criminalintent.data.repository

import com.asurspace.criminalintent.domain.repository.SearchCrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.data.local.dao.SearchCrimesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchCrimesRepositoryImpl @Inject constructor(
    private val crimesDao: SearchCrimesDao
) : SearchCrimesRepository {

    override fun findCrimeById(crimeId: Long): Flow<Crime> = crimesDao.findCrimeById(crimeId)
            .map { entity -> entity.toCrime() }
            .flowOn(Dispatchers.IO)

}