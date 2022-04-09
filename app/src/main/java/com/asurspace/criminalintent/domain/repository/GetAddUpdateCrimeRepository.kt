package com.asurspace.criminalintent.domain.repository

import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import kotlinx.coroutines.flow.Flow

interface GetAddUpdateCrimeRepository {

    fun getAllCrimes(onlyActive: Boolean? = false): Flow<MutableList<Crime>>

    fun findCrimeById(crimeId: Long): Flow<Crime>

    suspend fun addCrime(crime: Crime)

    suspend fun setSolved(solvedTuples: SetSolvedTuples)

    suspend fun updateCrime(crime: Crime?)

}