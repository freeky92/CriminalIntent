package com.asurspace.criminalintent.model.crimes

import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.core.content.contentValuesOf
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

    override suspend fun findCrimeByIdVMS(crimeId: Long): Crime? {
        return getCrimeById(crimeId).also { Log.i("return also", it?.title.toString()) }
    }

    override suspend fun addCrime(crime: Crime?) {
        createCrime(crime)
    }

    override suspend fun updateCrime(crimeId: Long?, crime: Crime?) {
        if (crime != null) {
            try {
                updateCrimeField(crimeId, crime)
            } catch (e: SQLiteException) {
                Log.i("SQLiteException", e.message.toString())
            }
        }
    }

    override suspend fun deleteCrime(crimeId: Long?): Int? {
        val count = delete(crimeId)
        Log.i("deleteCrime", count.toString())
        return count
    }

    override suspend fun clearCrimes() {
        TODO("Not yet implemented")
    }

    private fun queryCrimes(onlyActive: Boolean?): List<Crime>? {
        val cursor = cursorCrimes(onlyActive == true)

        return cursor.use {
            if (cursor.count == 0) {
                return@use null
            } else {

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
            suspect = cursor.getString(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_SUSPECT)),
            desciption = cursor.getString(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_DESCRIPTION)),
            creation_date = cursor.getLong(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_CREATION_DATE)),
            imageURI = cursor.getString(cursor.getColumnIndexOrThrow(CrimesTable.COLUMN_IMAGE_URI))
        )
    }

    private fun updateCrimeField(crimeId: Long?, crime: Crime?) {
        db.update(
            CrimesTable.TABLE_NAME,
            contentValuesOf(
                CrimesTable.COLUMN_SOLVED to crime?.solved,
                CrimesTable.COLUMN_TITLE to crime?.title,
                CrimesTable.COLUMN_SUSPECT to crime?.suspect,
                CrimesTable.COLUMN_DESCRIPTION to crime?.desciption,
                CrimesTable.COLUMN_CREATION_DATE to crime?.creation_date,
                CrimesTable.COLUMN_IMAGE_URI to crime?.imageURI,
            ),
            "${CrimesTable.COLUMN_ID} = ?",
            arrayOf(crimeId.toString())
        )
    }

    private fun createCrime(crime: Crime?) {

        val cDate = crime?.creation_date
            ?: System.currentTimeMillis().toString()

        try {
            db.insertOrThrow(
                CrimesTable.TABLE_NAME,
                null,
                contentValuesOf(
                    CrimesTable.COLUMN_SOLVED to crime?.solved,
                    CrimesTable.COLUMN_TITLE to crime?.title,
                    CrimesTable.COLUMN_SUSPECT to crime?.suspect,
                    CrimesTable.COLUMN_DESCRIPTION to crime?.desciption,
                    CrimesTable.COLUMN_CREATION_DATE to cDate,
                    CrimesTable.COLUMN_IMAGE_URI to crime?.imageURI
                )
            )
        } catch (e: SQLiteConstraintException) {
            Log.i("SQLite createCrime", e.message.toString())
        }
    }

    private fun delete(crimeId: Long?): Int? {
        db.delete(
            CrimesTable.TABLE_NAME,
            "${CrimesTable.COLUMN_ID} = ?",
            arrayOf(crimeId.toString())
        )
        // запрашиваем количество
        val cursor = db.rawQuery("SELECT  count(*) FROM ${CrimesTable.TABLE_NAME}", null)

        return cursor.use {
            if (cursor.count == 0) {
                return@use null
            }
            cursor.moveToFirst()
            it.getInt(0)
        }
    }


}