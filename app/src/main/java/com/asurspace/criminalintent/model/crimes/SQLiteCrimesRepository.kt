package com.asurspace.criminalintent.model.crimes

import android.database.sqlite.SQLiteDatabase
import com.asurspace.criminalintent.model.crimes.entities.Crime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SQLiteCrimesRepository(database: SQLiteDatabase, io: CoroutineDispatcher): CrimesRepository {

    override suspend fun getAllCrimes(): Flow<List<Crime>?> {
        TODO("Not yet implemented")
    }

    override suspend fun addCrime(crime: Crime) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCrime(id: String, pair: Pair<String, Any>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCrime(id: String?): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllCrimes() {
        TODO("Not yet implemented")
    }

}
