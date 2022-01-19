package com.asurspace.criminalintent.ui.crimes_list

import android.util.Log
import androidx.lifecycle.*
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimesListViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel(),
    LifecycleEventObserver {

    private val crimeDB: CrimesRepository = Repository.crimesRepo

    private var cList: List<Crime>? = emptyList()

    companion object {
        private const val CRIMELIST = "CRIMELIST"
    }

    private val _crimeListLD = savedStateHandle.getLiveData<MutableList<Crime>>(CRIMELIST)

    val crimeListLD: LiveData<MutableList<Crime>> = _crimeListLD

    init {
        Log.i("CLF", cList.toString())
        getCrimeList()
        Log.i("CLF", cList.toString())
    }

    private fun getCrimeList() {
        viewModelScope.launch(Dispatchers.IO) {
            cList = crimeDB.getAllCrimes(false)
            Log.i("get CLIST", cList.toString())
        }
        if (!savedStateHandle.contains(CRIMELIST)
            || savedStateHandle.get<List<Crime>?>(CRIMELIST) != cList
        ) {
            savedStateHandle.set(CRIMELIST, cList)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                Log.i("CLF", cList.toString())
                getCrimeList()
            }
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {}
            Lifecycle.Event.ON_PAUSE -> {}
            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {}
            Lifecycle.Event.ON_ANY -> {}
        }
    }


}