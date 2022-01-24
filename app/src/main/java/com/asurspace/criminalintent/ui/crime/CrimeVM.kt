package com.asurspace.criminalintent.ui.crime

import android.util.Log
import androidx.lifecycle.*
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.util.share
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimeVM(private val crimeId: Long, private val crimeDB: CrimesRepository) : ViewModel(),
    LifecycleEventObserver {

    private val _crimeLD = MutableLiveData<Crime>()
    val crimeLD = _crimeLD.share()

    private val _solvedLD = MutableLiveData<Int?>()
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

    private fun getCrime(crimeId: Long) {
        if (_crimeLD.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                _crimeLD.postValue(crimeDB.findCrimeByIdVMS(crimeId))
            }
        }
    }

    fun update() {
        setChanges()
        viewModelScope.launch(Dispatchers.IO) {
            crimeDB.updateCrime(crimeId, _crimeLD.value)
        }

    }

    fun setFields() {
        with(crimeLD.value) {
            _solvedLD.value = this?.solved
            _titleLD.value = this?.title
            _suspectLD.value = this?.suspect
            _descriptionLD.value = this?.desciption
            _cDateLD.value = this?.creation_date
            _imageUriLD.value = this?.imageURI
        }
    }

    fun setSolvedState(state: Boolean) {
        val digit = if (state) {
            1
        } else {
            0
        }

        if (solvedLD.value != digit) {
            _solvedLD.value = digit
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
            this?.creation_date = cDateLD.value
            this?.imageURI = imageUriLD.value
        }
        _crimeLD.value = updatedCrime?.toCrime()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                getCrime(crimeId)
                Log.i("ON_CREATE", _crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_START -> {
                Log.i("ON_START", _crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_RESUME -> {
                Log.i("ON_RESUME", _crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_PAUSE -> {

                Log.i("ON_RESUME", _crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_STOP -> {
                Log.i("ON_RESUME", _crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_DESTROY -> {
                setChanges()
                Log.i("ON_RESUME", _crimeLD.value?.title.toString())
            }
            Lifecycle.Event.ON_ANY -> {}
        }
    }

}