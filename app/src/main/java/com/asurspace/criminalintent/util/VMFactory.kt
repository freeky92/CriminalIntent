
package com.asurspace.criminalintent.util

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.ui.crime.CrimeVM


class VMFactory(
    owner: SavedStateRegistryOwner,
    private val crimeDb: CrimesRepository,
) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = (CrimeVM(handle, crimeDb) as T)

}
