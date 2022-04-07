package com.asurspace.criminalintent.ui.crimes_list

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CrimeItem(
    val solved: Boolean,
    val image: Uri,
    val title: String,
    val suspect: String,
) : Parcelable