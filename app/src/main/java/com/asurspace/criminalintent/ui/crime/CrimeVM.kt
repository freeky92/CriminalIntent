package com.asurspace.criminalintent.ui.crime

import android.util.Log
import androidx.lifecycle.*
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.entities.MutableCrime
import com.asurspace.criminalintent.util.share
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimeVM(private val crimeId: Long, private val crimeDB: CrimesRepository) : ViewModel(),
    LifecycleEventObserver {

    private var updatedCrime: MutableCrime? = null

    private val _crimeLD = MutableLiveData<Crime>()
    val crimeLD = _crimeLD.share()

    private fun getCrime(crimeId: Long) {
        if (crimeLD.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                _crimeLD.postValue(crimeDB.findCrimeByIdVMS(crimeId))
            }
        }
    }

    fun update() {
        setChanges()
        viewModelScope.launch(Dispatchers.IO) {
            crimeDB.updateCrime(crimeId, crimeLD.value)
        }

    }

    fun setSolvedState(state: Boolean) {
        val digit = if (state) {
            1
        } else {
            0
        }

        if (crimeLD.value?.solved != digit) {
            onAnyDataUpdated(1, argI = digit)
        }

    }

    fun setUpdatedTitle(title: String?) {
        if (crimeLD.value?.title != title) {
            onAnyDataUpdated(2, argS = title)
            Log.i("estUpDatedTilte", crimeLD.value?.title.toString())
        }
    }

    fun setUpdatedSuspect(suspect: String?) {
        if (crimeLD.value?.suspect != suspect) {
            onAnyDataUpdated(3, argS = suspect)
            Log.i("estUpDatedSuspect", crimeLD.value?.title.toString())
        }
    }

    fun setUpdatedDescription(description: String?) {
        if (crimeLD.value?.desciption != description) {
            onAnyDataUpdated(4, argS = description)
        }
    }

    fun setUpdatedImage(uri: String?) {
        if (crimeLD.value?.imageURI != uri) {
            onAnyDataUpdated(5, argS = uri)
        }
    }

    private fun onAnyDataUpdated(argNumber: Int, argS: String? = null, argI: Int? = null) {

        updatedCrime = crimeLD.value?.toMutableCrime()

        when (argNumber) {
            1 -> {
                updatedCrime?.solved = argI
            }
            2 -> {
                updatedCrime?.title = argS
            }
            3 -> {
                updatedCrime?.suspect = argS
            }
            4 -> {
                updatedCrime?.desciption = argS
            }
            5 -> {
                updatedCrime?.imageURI = argS
            }
        }

    }

    private fun setChanges() {
        _crimeLD.value = updatedCrime?.toCrime()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                getCrime(crimeId)
                Log.i("ON_CREATE", crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_START -> {
                Log.i("ON_START", crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_RESUME -> {
                Log.i("ON_RESUME", crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_PAUSE -> {

                Log.i("ON_RESUME", crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_STOP -> {
                Log.i("ON_RESUME", crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_DESTROY -> {
                setChanges()
                Log.i("ON_RESUME", crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_ANY -> {}
        }
    }

}