package com.asurspace.criminalintent.model.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppSQLiteHelper(
    private val applicationContext: Context
) : SQLiteOpenHelper(applicationContext, "crime_db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = applicationContext.assets.open("crime_db_init.sql").bufferedReader().use {
            it.readText()
        }

        sql.split(';')
            .filter { it.isNotBlank() }
            .forEach {
                db?.execSQL(it)
            }

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}