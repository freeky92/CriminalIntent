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

class CrimeVM(private val savedStateHandle: SavedStateHandle) : ViewModel(),
    LifecycleEventObserver {

    private val crimeDB: CrimesRepository = Repository.crimesRepo

    private val _crimeIdLD = savedStateHandle.getLiveData<Long>(CrimesTable.COLUMN_ID)
    val crimeIdLD = _crimeIdLD.share()

    private val _crimeLD = savedStateHandle.getLiveData<Crime>(CRIME)
    val crimeLD = _crimeLD.share()

    private val _changedCrimeLD = savedStateHandle.getLiveData<Crime>(CRIME)
    val changedCrimeLD = _changedCrimeLD.share()



    private val _onSolvedStateLD = savedStateHandle.getLiveData<Boolean>(CrimesTable.COLUMN_SOLVED)
    val onCheckBoxOn = _onSolvedStateLD.share()

    fun setCrimeOnVM(crimeId: Long) {
        if (crimeLD.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                _crimeLD.postValue(crimeDB.getCrimeByIdVMS(crimeId))
            }
        }
    }

    fun update() {
        viewModelScope.launch(Dispatchers.IO) {
            crimeDB.updateCrime(crimeIdLD.value ?: , )
        }
        TODO("Not yet implemented")
    }

    fun setSolvedState(state: Boolean) {
        if (_onSolvedStateLD.value != state) {
            _onSolvedStateLD.value = state
        }
    }



    fun setUpdatedTitle(title: String?) {

        TODO("Not yet implemented")
    }

    fun setUpdatedSuspect(suspect: String?) {

        TODO("Not yet implemented")
    }

    fun setUpdatedDescription(description: String?) {

        TODO("Not yet implemented")
    }

    fun onAnyDataUpdated(){
        changedCrimeLD

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