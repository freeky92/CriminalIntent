package com.asurspace.criminalintent.presentation.ui.crime.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.asurspace.criminalintent.common.utils.CRIME
import com.asurspace.criminalintent.common.utils.share
import com.asurspace.criminalintent.domain.usecase.remove.RemoveCrimeUseCase
import com.asurspace.criminalintent.domain.usecase.update.UpdateCrimeUseCase
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.entities.CrimeAdditional.emptyCrime
import com.asurspace.criminalintent.presentation.ui.state.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditCrimeVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val removeCrimeUseCase: RemoveCrimeUseCase,
    private val updateCrimeUseCase: UpdateCrimeUseCase
) : ViewModel(), LifecycleEventObserver {

    private val fromState = savedStateHandle.get<Crime>(SAVED_STATE_CRIME) ?: emptyCrime()
    private val getState = if (fromState != emptyCrime()) UIState.Success(fromState) else UIState.Empty

    private val _uiState = MutableStateFlow(getState)
    val uiState = _uiState.asStateFlow()

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

    private val _imageUriLD = MutableLiveData<Uri>()
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
        viewModelScope.launch {
            val num = withContext(Dispatchers.Default) {
                removeCrimeUseCase(_crimeId.value ?: 0)
            }
            _isRemoved.postValue(num)
        }
    }

    private fun update() {
        setChanges()
        viewModelScope.launch {
            updateCrimeUseCase(crimeLD.value)
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
            _imageUriLD.value = Uri.parse(this?.imageURI)
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

    fun setUpdatedImage(uri: Uri) {
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
            this?.imageURI = imageUriLD.value.toString()
        }
        if (crimeLD.value != updatedCrime?.toCrime()) {
            _crimeLD.value = updatedCrime?.toCrime()
        }
    }

    private fun initSSH() {
        if (!savedStateHandle.contains(CRIME)
            || savedStateHandle.get<Crime>(CRIME) != _crimeLD.value
        ) {
            savedStateHandle[CRIME] = _crimeLD.value
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
            }
            Lifecycle.Event.ON_START -> {
            }
            Lifecycle.Event.ON_RESUME -> {
            }
            Lifecycle.Event.ON_PAUSE -> {
                update()
            }
            Lifecycle.Event.ON_STOP -> {
            }
            Lifecycle.Event.ON_DESTROY -> {
                initSSH()
            }
            Lifecycle.Event.ON_ANY -> {
            }
        }
    }

    companion object {
        @JvmStatic
        private val TAG = "EditCrimeVM"

        @JvmStatic
        private val SAVED_STATE_CRIME = "SAVED_STATE_CRIME"
    }

}