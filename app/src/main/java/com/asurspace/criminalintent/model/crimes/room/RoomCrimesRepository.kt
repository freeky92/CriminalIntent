package com.asurspace.criminalintent.model.crimes.room

import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples

class RoomCrimesRepository(private val crimesDao: CrimesDao) : CrimesRepository {

    override suspend fun getAllCrimes(onlyActive: Boolean?): MutableList<Crime> {
        return crimesDao.getAllCrimes().map { crimes -> crimes.toCrime() }.toMutableList()
    }

    override suspend fun findCrimeById(crimeId: Long): Crime? {
        return crimesDao.findCrimeById(crimeId)?.toCrime()
    }

    override suspend fun addCrime(crime: Crime) {
        crimesDao.addCrime(crime.toCrimeDbEntity())
    }

    override suspend fun setSolved(solvedTuples: SetSolvedTuples) {
        crimesDao.setSolved(solvedTuples)
    }

    override suspend fun updateCrime(crime: Crime?) {
        crime?.let { crimesDao.updateCrime(it.toCrimeDbEntity()) }
    }

    override suspend fun deleteCrime(crimeId: Long) {
        return crimesDao.deleteCrime(crimeId)
    }

    override suspend fun clearCrimes() {
        TODO("Not yet implemented")
    }
}