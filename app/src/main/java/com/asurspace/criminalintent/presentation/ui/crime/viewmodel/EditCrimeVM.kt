package com.asurspace.criminalintent.presentation.ui.crime.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.asurspace.criminalintent.common.utils.share
import com.asurspace.criminalintent.domain.usecase.remove.RemoveCrimeUseCase
import com.asurspace.criminalintent.domain.usecase.update.UpdateCrimeUseCase
import com.asurspace.criminalintent.data.model.crimes.entities.Crime
import com.asurspace.criminalintent.data.model.crimes.entities.CrimeAdditional.emptyCrime
import com.asurspace.criminalintent.presentation.ui.state.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditCrimeVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val removeCrimeUseCase: RemoveCrimeUseCase,
    private val updateCrimeUseCase: UpdateCrimeUseCase
) : ViewModel(), LifecycleEventObserver {

    private val state = savedStateHandle.get<Crime>(SAVED_STATE_CRIME)

    private var currentCrime = state ?: emptyCrime()

    private val _uiState = MutableStateFlow<UIState<Crime>>(UIState.Empty)
    val uiState = _uiState.asStateFlow()

    private val _isRemoved = MutableLiveData<Int>()
    val isRemoved = _isRemoved.share()

    fun setCrime(crime: Crime) {
        if (crime != currentCrime) {
            currentCrime = crime
            updateUIState()
        }
    }

    fun remove() {
        viewModelScope.launch {
            val num = withContext(Dispatchers.Default) {
                removeCrimeUseCase(currentCrime.id ?: 0)
            }
            _isRemoved.postValue(num)
        }
    }

    fun setSolvedState(state: Boolean) {
        if (currentCrime.solved != state) {
            currentCrime = currentCrime.copy(solved = state)
        }
    }

    fun setUpdatedTitle(title: String?) {
        if (currentCrime.title != title) {
            currentCrime = currentCrime.copy(title = title)
        }
    }

    fun setUpdatedSuspect(suspect: String?) {
        if (currentCrime.suspect != suspect) {
            currentCrime = currentCrime.copy(suspect = suspect)
        }
    }

    fun setUpdatedDescription(description: String?) {
        if (currentCrime.description != description) {
            currentCrime = currentCrime.copy(description = description)
        }
    }

    fun setUpdatedImage(uri: Uri) {
        if (currentCrime.imageURI != uri.toString()) {
            currentCrime = currentCrime.copy(imageURI = uri.toString())
        }
        updateUIState()
    }

    private fun update() {
        viewModelScope.launch {
            updateCrimeUseCase(currentCrime)
        }
    }

    private fun updateUIState(){
        _uiState.update { UIState.Success(currentCrime) }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {

            Lifecycle.Event.ON_PAUSE -> {
                update()
                updateUIState()
            }
            Lifecycle.Event.ON_DESTROY -> {
                savedStateHandle.set(SAVED_STATE_CRIME, currentCrime)
            }
            else -> {}
        }
    }

    companion object {
        @JvmStatic
        private val TAG = "EditCrimeVM"

        @JvmStatic
        private val SAVED_STATE_CRIME = "SAVED_STATE_CRIME"
    }

}