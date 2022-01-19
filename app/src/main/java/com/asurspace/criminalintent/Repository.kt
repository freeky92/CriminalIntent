package com.asurspace.criminalintent

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.SQLiteCrimesRepository
import com.asurspace.criminalintent.model.sqlite.AppSQLiteHelper
import kotlinx.coroutines.Dispatchers

object Repository {
    private lateinit var applicationContext: Context

    // метод создает DB по
    private val database: SQLiteDatabase by lazy<SQLiteDatabase> {
        AppSQLiteHelper(applicationContext).writableDatabase
    }

    val crimesRepo: CrimesRepository by lazy {
        SQLiteCrimesRepository(database, Dispatchers.IO)
    }

    fun init(context: Context) {
        applicationContext = context
    }

}