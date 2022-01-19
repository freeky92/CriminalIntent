package com.asurspace.criminalintent.ui.crimes_list

import android.util.Log
import androidx.lifecycle.*
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.share
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimesListVM(private val savedStateHandle: SavedStateHandle) : ViewModel(),
    LifecycleEventObserver {

    private val crimeDB: CrimesRepository = Repository.crimesRepo

    companion object {
        private const val CRIMELIST = "CRIMELIST"
    }

    private val _crimeListLD = savedStateHandle.getLiveData<List<Crime>?>(CRIMELIST)
    val crimeListLD = _crimeListLD.share()

    init {
        getCrimeList()
    }

    private fun getCrimeList() {
        if (crimeListLD.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                val list = crimeDB.getAllCrimes(false)
                _crimeListLD.postValue(list?.reversed())
            }
        }
    }

    private fun initSSH() {
        if (!savedStateHandle.contains(CRIMELIST)
            || savedStateHandle.get<List<Crime>?>(CRIMELIST) != crimeListLD.value
        ) {
            savedStateHandle.set(CRIMELIST, crimeListLD.value)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                getCrimeList()
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