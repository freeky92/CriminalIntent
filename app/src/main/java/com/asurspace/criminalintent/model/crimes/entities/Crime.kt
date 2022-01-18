package com.asurspace.criminalintent.model.crimes.entities

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Crime(
    val id: Long?,
    val solved: Boolean?,
    val title: String?,
    val suspectName: String?,
    val desciption: String?,
    val creation_date: Long?,
    val imageURI: Uri?,
) : Parcelable {
    companion object {
        const val UNKNOWN_CREATION_DATE = 0L
    }
}