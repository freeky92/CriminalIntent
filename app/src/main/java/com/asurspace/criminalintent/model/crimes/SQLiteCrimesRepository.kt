package com.asurspace.criminalintent.model.crimes

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.sqlite.AppSQLiteContract.CrimesTable
import kotlinx.coroutines.CoroutineDispatcher

class SQLiteCrimesRepository(
    private val db: SQLiteDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : CrimesRepository {

    override suspend fun getAllCrimes(onlyActive: Boolean?): List<Crime>? {
        return queryCrimes(onlyActive)
    }

    override suspend fun getCrimeByIdF(crimeId: Long): Crime? {
        return getCrimeById(crimeId)
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

    private fun queryCrimes(onlyActive: Boolean?): List<Crime>? {
        val cursor = cursorCrimes(onlyActive == true)

        return if (cursor.count == 0) {
            return null
        } else {
            cursor.use {
                val list = mutableListOf<Crime>()
                while (cursor.moveToNext()) {
                    list.add(parseCrime(cursor))
                }
                return@use list
            }
        }
    }

    private fun cursorCrimes(onlyActive: Boolean): Cursor {
        return if (onlyActive) {
            val sql = "SELECT * " +
                    "From ${CrimesTable.TABLE_NAME} " +
                    "WHERE ${CrimesTable.COLUMN_SOLVED} = 0 "
            db.rawQuery(sql, null)
        } else {
            db.query(
                CrimesTable.TABLE_NAME,
                null,
                null,
                null,
                null, null, null
            )
            //return db.rawQuery("SELECT * FROM ${CrimesTable.TABLE_NAME}", null)
        }
    }

    private fun getCrimeById(crimeId: Long): Crime? {
        val cursor = db.query(
            CrimesTable.TABLE_NAME,
            arrayOf(
                CrimesTable.COLUMN_ID,
                CrimesTable.COLUMN_SOLVED,
                CrimesTable.COLUMN_TITLE,
                CrimesTable.COLUMN_SUSPECT,
                CrimesTable.COLUMN_DESCRIPTION,
                CrimesTable.COLUMN_CREATION_DATE,
                CrimesTable.COLUMN_IMAGE_URI
            ),
            "${CrimesTable.COLUMN_ID} = ?",
            arrayOf(crimeId.toString()),
            null, null, null
        )

        return cursor.use {
            if (cursor.count == 0) {
                return@use null
            }
            cursor.moveToFirst()
            parseCrime(it)
        }
    }

    private fun parseCrime(cursor: Cursor): Crime {
        return Crime(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_ID)),
            solved = cursor.getInt(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_SOLVED)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_TITLE)),
            suspectName = cursor.getString(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_SUSPECT)),
            desciption = cursor.getString(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_DESCRIPTION)),
            creation_date = cursor.getLong(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_CREATION_DATE)),
            imageURI = cursor.getString(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_IMAGE_URI))
        )
    }


}