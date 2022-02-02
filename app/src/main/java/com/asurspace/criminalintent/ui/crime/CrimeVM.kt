package com.asurspace.criminalintent.ui.crime

import android.util.Log
import androidx.lifecycle.*
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.util.CRIME
import com.asurspace.criminalintent.util.share
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimeVM(
    private val savedStateHandle: SavedStateHandle,
    private val crimeDB: CrimesRepository
) : ViewModel(), LifecycleEventObserver {

    private var _crimeId = MutableLiveData<Long?>()

    private val _crimeLD = savedStateHandle.getLiveData<Crime?>(CRIME)
    val crimeLD = _crimeLD.share()

    private val _solvedLD = MutableLiveData<Boolean?>()
    val solvedLD = _solvedLD.share()

    private val _titleLD = MutableLiveData<String?>()
    val titleLD = _titleLD.share()

    private val _suspectLD = MutableLiveData<String?>()
    val suspectLD = _suspectLD.share()

    private val _descriptionLD = MutableLiveData<String?>()
    val descriptionLD = _descriptionLD.share()

    private val _cDateLD = MutableLiveData<Long?>()
    val cDateLD = _cDateLD.share()

    private val _imageUriLD = MutableLiveData<String?>()
    val imageUriLD = _imageUriLD.share()

    private val _isRemoved = MutableLiveData<Int>()
    val isRemoved = _isRemoved.share()

    fun setCrime(crime: Crime?) {
        _crimeLD.value = crime
    }

    fun setCrimeId(id: Long?) {
        if (_crimeId.value != id) {
            _crimeId.value = id
        }
    }

    fun remove() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRemoved.postValue(crimeDB.deleteCrime(_crimeId.value ?: 0))
        }
    }

    private fun update() {
        setChanges()
        viewModelScope.launch(Dispatchers.IO) {
            crimeDB.updateCrime(crimeLD.value)
        }
    }

    fun setFields() {
        with(crimeLD.value) {
            _crimeId.value = this?.id
            _solvedLD.value = this?.solved
            _titleLD.value = this?.title
            _suspectLD.value = this?.suspect
            _descriptionLD.value = this?.description
            _cDateLD.value = this?.creationDate
            _imageUriLD.value = this?.imageURI
        }
    }

    fun setSolvedState(state: Boolean) {
        if (solvedLD.value != state) {
            _solvedLD.value = state
        }
    }

    fun setUpdatedTitle(title: String?) {
        if (titleLD.value != title) {
            _titleLD.value = title
        }
    }

    fun setUpdatedSuspect(suspect: String?) {
        if (suspectLD.value != suspect) {
            _suspectLD.value = suspect
        }
    }

    fun setUpdatedDescription(description: String?) {
        if (descriptionLD.value != description) {
            _descriptionLD.value = description
        }
    }

    fun setUpdatedImage(uri: String?) {
        if (imageUriLD.value != uri) {
            _imageUriLD.value = uri
        }
    }

    private fun setChanges() {
        val updatedCrime = crimeLD.value?.toMutableCrime()
        with(updatedCrime) {
            this?.solved = solvedLD.value
            this?.title = titleLD.value
            this?.suspect = suspectLD.value
            this?.desciption = descriptionLD.value
            this?.creationDate = cDateLD.value
            this?.imageURI = imageUriLD.value
        }
        if (crimeLD.value != updatedCrime?.toCrime()) {
            _crimeLD.value = updatedCrime?.toCrime()
        }
    }

    private fun initSSH() {
        if (!savedStateHandle.contains(CRIME)
            || savedStateHandle.get<Crime>(CRIME) != _crimeLD.value
        ) {
            savedStateHandle.set(CRIME, _crimeLD.value)
        }
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                Log.i("vmOnCreate", crimeLD.value.toString())
            }
            Lifecycle.Event.ON_START -> {
                Log.i("vmOnStart", crimeLD.value.toString())
                Log.i("vmOnStart", _crimeId.value.toString())
            }
            Lifecycle.Event.ON_RESUME -> {
                Log.i("vmOnResume", crimeLD.value.toString())
                Log.i("vmOnResume", _crimeId.value.toString())
            }
            Lifecycle.Event.ON_PAUSE -> {
                update()
                Log.i("vmOnCreate", crimeLD.value.toString())
                Log.i("vmOnCreate", _crimeId.value.toString())
            }
            Lifecycle.Event.ON_STOP -> {
                Log.i("vmOnStop", crimeLD.value.toString())

            }
            Lifecycle.Event.ON_DESTROY -> {
                initSSH()
                Log.i("vmOnDestroy", crimeLD.value.toString())
            }
            Lifecycle.Event.ON_ANY -> {
            }
        }
    }


}