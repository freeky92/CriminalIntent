package com.asurspace.criminalintent.ui.create_crime

import androidx.lifecycle.*
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.sqlite.AppSQLiteContract.CrimesTable
import com.asurspace.criminalintent.util.share
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateCrimeVM(private val savedStateHandle: SavedStateHandle) : ViewModel(),
    LifecycleEventObserver {

    private val crimeDB: CrimesRepository = Repository.crimesRepo

    private val _titleLD = savedStateHandle.getLiveData<String>(CrimesTable.COLUMN_TITLE)
    val titleLD = _titleLD.share()

    private val _suspectLD = savedStateHandle.getLiveData<String>(CrimesTable.COLUMN_SUSPECT)
    val suspectLD = _suspectLD.share()

    private val _description = savedStateHandle.getLiveData<String>(CrimesTable.COLUMN_DESCRIPTION)
    val descriptionLD = _description.share()

    private val _imageUriLD = savedStateHandle.getLiveData<String>(CrimesTable.COLUMN_DESCRIPTION)
    val imageUriLD = _imageUriLD.share()

    fun setUpdatedTitle(title: String?) {
        _titleLD.value = title
    }

    fun setUpdatedSuspect(suspect: String?) {
        _suspectLD.value = suspect
    }

    fun setUpdatedDescription(description: String?) {
        _description.value = description
    }

    fun setUpdatedImage(uri: String?) {
        if (imageUriLD.value != uri) {
            _imageUriLD.value = uri
        }
    }

    fun addCrime() {
        viewModelScope.launch(Dispatchers.IO) {
            crimeDB.addCrime(
                Crime(
                    id = null,
                    solved = 0,
                    title = titleLD.value,
                    suspect = suspectLD.value,
                    desciption = descriptionLD.value,
                    creation_date = System.currentTimeMillis(),
                    imageURI = imageUriLD.value
                )
            )
        }
    }

    private fun initSSH() {
        if (!savedStateHandle.contains(CrimesTable.COLUMN_TITLE)
            || savedStateHandle.get<String>(CrimesTable.COLUMN_TITLE) != titleLD.value
        ) {
            savedStateHandle.set(CrimesTable.COLUMN_TITLE, titleLD.value)
        }

        if (!savedStateHandle.contains(CrimesTable.COLUMN_SUSPECT)
            || savedStateHandle.get<String>(CrimesTable.COLUMN_SUSPECT) != suspectLD.value
        ) {
            savedStateHandle.set(CrimesTable.COLUMN_SUSPECT, suspectLD.value)
        }

        if (!savedStateHandle.contains(CrimesTable.COLUMN_DESCRIPTION)
            || savedStateHandle.get<String>(CrimesTable.COLUMN_DESCRIPTION) != descriptionLD.value
        ) {
            savedStateHandle.set(CrimesTable.COLUMN_DESCRIPTION, descriptionLD.value)
        }

        if (!savedStateHandle.contains(CrimesTable.COLUMN_IMAGE_URI)
            || savedStateHandle.get<String>(CrimesTable.COLUMN_IMAGE_URI) != imageUriLD.value
        ) {
            savedStateHandle.set(CrimesTable.COLUMN_IMAGE_URI, imageUriLD.value)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {

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