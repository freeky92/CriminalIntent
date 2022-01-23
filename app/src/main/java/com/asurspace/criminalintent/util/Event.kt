package com.asurspace.criminalintent.util

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

object FragmentNameList {
    const val CRIMES_LIST_FRAGMENT = "CrimesListFragment"
    const val CRIME_FRAGMENT = "CrimeFragment"
    const val CREATE_CRIME_FRAGMENT = "CreateCrimeFragment"
}

// --- helper methods / aliases

/**
 * Convert mutable live-data into non-mutable live-data.
 */
fun <T> MutableLiveData<T>.share(): LiveData<T> = this

@SuppressLint("ConstantLocale")
val dateFormat = SimpleDateFormat("HH:mm:ss EEEE dd.MM.yyyy ", Locale.getDefault())

const val RESULT = "RESULT"
const val CRIME = "CRIME"
const val CHANGED_CRIME = "CHANGED_CRIME"
