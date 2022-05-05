package com.asurspace.criminalintent.data.model.crimes.room.entyties

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.asurspace.criminalintent.data.model.crimes.entities.Crime

@Entity(
    tableName = "crimes"
)
data class CrimeDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val solved: Boolean,
    val title: String,
    val suspect: String,
    val description: String,
    @ColumnInfo(name = "creation_date") val creationDate: Long,
    @ColumnInfo(name = "image_uri") val imageURI: String,
) {
    fun toCrime(): Crime = Crime(
        id = id,
        solved = solved,
        title = title,
        suspect = suspect,
        description = description,
        creationDate = creationDate,
        imageURI = imageURI,
    )
}