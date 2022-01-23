package com.asurspace.criminalintent.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asurspace.criminalintent.util.share

class SharedVM : ViewModel() {

    private val _crimeId = MutableLiveData<Long?>()
    val crimeId = _crimeId.share()

    fun setCrimeId(crimeId: Long?) {
        _crimeId.value = crimeId
    }
}