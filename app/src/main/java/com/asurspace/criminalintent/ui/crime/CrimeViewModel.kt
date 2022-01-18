package com.asurspace.criminalintent.ui.crime

import androidx.lifecycle.*

class CrimeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel(),
    LifecycleEventObserver {

    companion object {
        private const val SOLVEDCB = "SOLVEDCB"
    }

    private val _onSolvedStateLD = savedStateHandle.getLiveData<Boolean>(SOLVEDCB)
    val onCheckBoxOn: LiveData<Boolean> = _onSolvedStateLD

    init {
        if (!savedStateHandle.contains(SOLVEDCB)) {
            savedStateHandle.set(SOLVEDCB, false)
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