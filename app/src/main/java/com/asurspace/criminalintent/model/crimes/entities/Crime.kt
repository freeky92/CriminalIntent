package com.asurspace.criminalintent.model.crimes.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Crime(
    val id: Long?,
    val solved: Int?,
    val title: String?,
    val suspectName: String?,
    val desciption: String?,
    val creation_date: Long?,
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
            suspectName = this.suspectName,
            desciption = this.desciption,
            creation_date = this.creation_date,
            imageURI = this.imageURI
        )
    }


}

@Parcelize
data class MutableCrime(
    var id: Long?,
    var solved: Int?,
    var title: String?,
    var suspectName: String?,
    var desciption: String?,
    var creation_date: Long?,
    var imageURI: String?
) : Parcelable {

    fun toCrime(): Crime {
        return Crime(
            id = this.id,
            solved = this.solved,
            title = this.title,
            suspectName = this.suspectName,
            desciption = this.desciption,
            creation_date = this.creation_date,
            imageURI = this.imageURI
        )
    }
}