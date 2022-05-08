package com.asurspace.criminalintent.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.asurspace.criminalintent.common.utils.CrimesTable.TABLE_NAME
import com.asurspace.criminalintent.data.model.crimes.room.entyties.CrimeDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchCrimesDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun findCrimeById(id: Long): Flow<CrimeDbEntity>

}