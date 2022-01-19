package com.asurspace.criminalintent.ui.crime

import androidx.lifecycle.*
import com.asurspace.criminalintent.CRIME
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.sqlite.AppSQLiteContract.CrimesTable
import com.asurspace.criminalintent.share
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel(),
    LifecycleEventObserver {

    private val crimeDB: CrimesRepository = Repository.crimesRepo

    private val _crimeLD = savedStateHandle.getLiveData<Crime>(CRIME)
    val crimeLD = _crimeLD.share()

    private val _crimeIdLD = savedStateHandle.getLiveData<Long>(CrimesTable.COLUMN_ID)
    val crimeIdLD = _crimeIdLD.share()

    private val _onSolvedStateLD = savedStateHandle.getLiveData<Boolean>(CrimesTable.COLUMN_SOLVED)
    val onCheckBoxOn = _onSolvedStateLD.share()

    fun setCrimeOnVM(crimeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            //crimeDB.getCrimeByIdF()
        }

        _crimeIdLD.value = crimeId
        // _onSolvedStateLD.value =
    }


    init {
        if (!savedStateHandle.contains(CrimesTable.COLUMN_SOLVED)) {
            savedStateHandle.set(CrimesTable.COLUMN_SOLVED, false)
        }
        if (!savedStateHandle.contains(CrimesTable.COLUMN_SOLVED)) {
            savedStateHandle.set(CrimesTable.COLUMN_SOLVED, false)
        }
        if (!savedStateHandle.contains(CrimesTable.COLUMN_SOLVED)) {
            savedStateHandle.set(CrimesTable.COLUMN_SOLVED, false)
        }
    }

    fun setSolvedState(state: Boolean) {
        if (_onSolvedStateLD.value != state) {
            _onSolvedStateLD.value = state
        }
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        TODO("Not yet implemented")
    }

}