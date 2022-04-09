package com.asurspace.criminalintent.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.asurspace.criminalintent.common.utils.CrimesTable

@Dao
interface RemoveCrimeDao {

    @Query("DELETE FROM ${CrimesTable.TABLE_NAME} WHERE id =:id")
    suspend fun deleteCrime(id: Long): Int

    @Query("DELETE FROM ${CrimesTable.TABLE_NAME}")
    suspend fun clearTable()

}