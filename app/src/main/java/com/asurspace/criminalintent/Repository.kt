package com.asurspace.criminalintent

import android.content.Context
import androidx.room.Room
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.room.RoomCrimesRepository
import com.asurspace.criminalintent.model.room.AppDatabase

object Repository {
    private lateinit var applicationContext: Context

    // метод создает RoomDb
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database.db")
            //.createFromAsset("init_database.db")
            //.fallbackToDestructiveMigration()
            .build()
    }

    val crimesRepo: CrimesRepository by lazy {
        RoomCrimesRepository(database.getCrimesDao())
    }

    fun init(context: Context) {
        applicationContext = context
    }

}