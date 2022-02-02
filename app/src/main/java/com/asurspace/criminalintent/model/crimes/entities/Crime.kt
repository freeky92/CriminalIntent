package com.asurspace.criminalintent.model.crimes.entities

import android.os.Parcelable
import com.asurspace.criminalintent.model.crimes.room.entyties.CrimeDbEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Crime(
    val id: Long?,
    val solved: Boolean?,
    val title: String?,
    val suspect: String?,
    val description: String?,
    val creationDate: Long?,
    val imageURI: String?
) : Parcelable {
    companion object {
        const val UNKNOWN_CREATION_DATE = 0L
    }

    fun toMutableCrime(): MutableCrime {
        return MutableCrime(
            id = this.id,
            solved = this.solved,
            title = this.title,
            suspect = this.suspect,
            desciption = this.description,
            creationDate = this.creationDate,
            imageURI = this.imageURI
        )
    }

    fun toCrimeDbEntity(): CrimeDbEntity {
        return CrimeDbEntity(
            id = id ?: 0,
            solved = solved ?: false,
            title = title ?: "",
            suspect = suspect ?: "",
            description = description ?: "",
            creationDate = creationDate ?: 0,
            imageURI = imageURI ?: ""
        )
    }

    override fun toString(): String {
        return super.toString()
            .plus("$id, $solved, $title, $suspect, $description, $creationDate, $imageURI.")
    }
}

@Parcelize
data class MutableCrime(
    var id: Long?,
    var solved: Boolean?,
    var title: String?,
    var suspect: String?,
    var desciption: String?,
    var creationDate: Long?,
    var imageURI: String?
) : Parcelable {

    fun toCrime(): Crime {
        return Crime(
            id = this.id,
            solved = this.solved,
            title = this.title,
            suspect = this.suspect,
            description = this.desciption,
            creationDate = this.creationDate,
            imageURI = this.imageURI
        )
    }
}