package com.asurspace.criminalintent.ui.crime

import android.util.Log
import androidx.lifecycle.*
import com.asurspace.criminalintent.CHANGED_CRIME
import com.asurspace.criminalintent.CRIME
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.sqlite.AppSQLiteContract.CrimesTable
import com.asurspace.criminalintent.share
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimeVM(private val savedStateHandle: SavedStateHandle) : ViewModel(),
    LifecycleEventObserver {

    private val crimeDB: CrimesRepository = Repository.crimesRepo

    private val _crimeIdLD = savedStateHandle.getLiveData<Long>(CrimesTable.COLUMN_ID)
    val crimeIdLD = _crimeIdLD.share()

    private val crimeLD = savedStateHandle.getLiveData<Crime>(CRIME)

    private val _changedCrimeLD = savedStateHandle.getLiveData<Crime>(CHANGED_CRIME)
    val changedCrimeLD = _changedCrimeLD.share()

    private val _onCheckBoxOnLD = savedStateHandle.getLiveData<Int>(CrimesTable.COLUMN_SOLVED)
    val onCheckBoxOn = _onCheckBoxOnLD.share()

    fun setCrimeOnVM(crimeId: Long) {
        Log.i("VM setCrime", crimeId.toString())
        if (crimeLD.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                crimeLD.postValue(crimeDB.findCrimeByIdVMS(crimeId))
            }
        }
    }

    fun update() {
        viewModelScope.launch(Dispatchers.IO) {
            crimeDB.updateCrime(crimeIdLD.value, changedCrimeLD.value)
        }
        TODO("Not yet implemented")
    }

    fun setSolvedState(state: Boolean) {
        val digit = if (state) {
            1
        } else {
            0
        }

        if (_onCheckBoxOnLD.value != digit) {
            _onCheckBoxOnLD.value = digit
        }

        onAnyDataUpdated(1, argI = digit)
    }


    fun setUpdatedTitle(title: String?) {
        onAnyDataUpdated(2, argS = title)
    }

    fun setUpdatedSuspect(suspect: String?) {
        onAnyDataUpdated(3, argS = suspect)
    }

    fun setUpdatedDescription(description: String?) {
        onAnyDataUpdated(4, argS = description)
    }

    fun setUpdatedImage(uri: String?) {
        onAnyDataUpdated(5, argS = uri)
    }

    private fun onAnyDataUpdated(argNumber: Int, argS: String? = null, argI: Int? = null) {

        val crime = if (changedCrimeLD.value == null) {
            crimeLD.value?.toMutableCrime()
        } else {
            changedCrimeLD.value?.toMutableCrime()
        }

        when (argNumber) {
            1 -> {
                crime?.solved = argI
                _changedCrimeLD.value = crime?.toCrime()
            }
            2 -> {
                crime?.title = argS
                _changedCrimeLD.value = crime?.toCrime()
            }
            3 -> {
                crime?.suspectName = argS
                _changedCrimeLD.value = crime?.toCrime()
            }
            4 -> {
                crime?.desciption = argS
                _changedCrimeLD.value = crime?.toCrime()
            }
            5 -> {
                crime?.imageURI = argS
                _changedCrimeLD.value = crime?.toCrime()
            }

        }

        /*when (argNumber) {
            1 -> {
                _changedCrimeLD.value = Crime(
                    id = crime?.id,
                    solved = argI,
                    title = crime?.title,
                    suspectName = crime?.suspectName,
                    desciption = crime?.desciption,
                    creation_date = crime?.creation_date,
                    imageURI = crime?.imageURI,
                )
            }
            2 -> {
                _changedCrimeLD.value = Crime(
                    id = crime?.id,
                    solved = crime?.solved,
                    title = argS,
                    suspectName = crime?.suspectName,
                    desciption = crime?.desciption,
                    creation_date = crime?.creation_date,
                    imageURI = crime?.imageURI,
                )
            }
            3 -> {
                _changedCrimeLD.value = Crime(
                    id = crime?.id,
                    solved = crime?.solved,
                    title = crime?.title,
                    suspectName = argS,
                    desciption = crime?.desciption,
                    creation_date = crime?.creation_date,
                    imageURI = crime?.imageURI,
                )
            }
            4 -> {
                _changedCrimeLD.value = Crime(
                    id = crime?.id,
                    solved = crime?.solved,
                    title = crime?.title,
                    suspectName = crime?.suspectName,
                    desciption = argS,
                    creation_date = crime?.creation_date,
                    imageURI = crime?.imageURI,
                )
            }
            5 -> {
                _changedCrimeLD.value = Crime(
                    id = crime?.id,
                    solved = crime?.solved,
                    title = crime?.title,
                    suspectName = crime?.suspectName,
                    desciption = crime?.desciption,
                    creation_date = crime?.creation_date,
                    imageURI = argS,
                )
            }
        }*/

    }

    private fun initSSH() {
        TODO()
        /*if (!savedStateHandle.contains(CrimesTable.COLUMN_TITLE)
            || savedStateHandle.get<String>(CrimesTable.COLUMN_TITLE) != titleLD.value
        ) {
            savedStateHandle.set(CrimesTable.COLUMN_TITLE, titleLD.value)
        }

        if (!savedStateHandle.contains(CrimesTable.COLUMN_SUSPECT)
            || savedStateHandle.get<String>(CrimesTable.COLUMN_SUSPECT) != suspectLD.value
        ) {
            savedStateHandle.set(CrimesTable.COLUMN_SUSPECT, suspectLD.value)
        }

        if (!savedStateHandle.contains(CrimesTable.COLUMN_DESCRIPTION)
            || savedStateHandle.get<String>(CrimesTable.COLUMN_DESCRIPTION) != descriptionLD.value
        ) {
            savedStateHandle.set(CrimesTable.COLUMN_DESCRIPTION, descriptionLD.value)
        }*/
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {

            }
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {}
            Lifecycle.Event.ON_PAUSE -> {}
            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {
                initSSH()
            }
            Lifecycle.Event.ON_ANY -> {}
        }
    }

}