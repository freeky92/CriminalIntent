package com.asurspace.criminalintent.ui.crimes_list

import androidx.lifecycle.*
import com.asurspace.criminalintent.util.CrimesVDB
import com.asurspace.criminalintent.model.crimes.entities.Crime

class CrimesListViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel(),
    LifecycleEventObserver {

    companion object {
        private const val CRIMELIST = "CRIMELIST"
    }

    private val _crimeListLD = savedStateHandle.getLiveData<MutableList<Crime>>(CRIMELIST)

    val crimeListLD: LiveData<MutableList<Crime>> = _crimeListLD

    init {
        if (!savedStateHandle.contains(CRIMELIST)) {
            savedStateHandle.set(CRIMELIST, CrimesVDB.crimesList)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {}
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {}
            Lifecycle.Event.ON_PAUSE -> {}
            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {}
            Lifecycle.Event.ON_ANY -> {}
        }
    }


}