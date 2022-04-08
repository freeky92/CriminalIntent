package com.asurspace.criminalintent.common.utils

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*

class Event<T>(
    value: T
) {

    private var _value: T? = value

    fun get(): T? = _value.also { _value = null }

}

// --- helper methods / aliases

/**
 * Convert mutable live-data into non-mutable live-data.
 */
fun <T> MutableLiveData<T>.share(): LiveData<T> = this

@SuppressLint("ConstantLocale")
val dateFormat = SimpleDateFormat("HH:mm:ss EEEE dd.MM.yyyy ", Locale.getDefault())


const val CRIMES_LIST_FRAGMENT = "CrimesListFragment"
const val CRIME_FRAGMENT = "CrimeFragment"
const val CREATE_CRIME_FRAGMENT = "CreateCrimeFragment"
const val PREVIEW_FRAGMENT = "PreviewFragment"
// obj
const val CRIME = "CRIME"
const val IMAGE = "IMAGE"
// dest
const val TO_CRIME_FRAGMENT = "TO_CRIME_FRAGMENT"
const val PREVIEW = "PREVIEW"

object CrimesTable {
    const val TABLE_NAME = "crimes"
    const val DB_NAME = "database.db"

    const val COLUMN_ID = "id"
    const val COLUMN_SOLVED = "solved"
    const val COLUMN_TITLE = "title"
    const val COLUMN_SUSPECT = "suspect"
    const val COLUMN_DESCRIPTION = "description"
    const val COLUMN_CREATION_DATE = "creation_date"
    const val COLUMN_IMAGE_URI = "image_uri"
}
