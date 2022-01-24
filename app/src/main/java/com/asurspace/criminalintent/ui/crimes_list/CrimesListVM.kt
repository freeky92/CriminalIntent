package com.asurspace.criminalintent.ui.crimes_list

import androidx.lifecycle.*
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.util.share
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimesListVM(private val crimeDB: CrimesRepository) : ViewModel(),
    LifecycleEventObserver {

    companion object {
        private const val CRIMELIST = "CRIMELIST"
    }

    private val _crimeListLD = MutableLiveData<List<Crime>?>()
    val crimeListLD = _crimeListLD.share()

    init {
        if (crimeListLD.value == null) {
            getCrimeList()
        }
    }

    private fun getCrimeList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = crimeDB.getAllCrimes(false)
            _crimeListLD.postValue(list?.reversed())
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
            }
            Lifecycle.Event.ON_START -> {
            }
            Lifecycle.Event.ON_RESUME -> {
                getCrimeList()
            }
            Lifecycle.Event.ON_PAUSE -> {}
            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {
            }
            Lifecycle.Event.ON_ANY -> {}
        }
    }


}