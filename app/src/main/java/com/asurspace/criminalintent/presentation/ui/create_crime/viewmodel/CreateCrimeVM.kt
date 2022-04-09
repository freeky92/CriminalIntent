package com.asurspace.criminalintent.presentation.ui.create_crime.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.asurspace.criminalintent.common.utils.CrimesTable
import com.asurspace.criminalintent.common.utils.share
import com.asurspace.criminalintent.domain.usecase.create.CreateCrimeUseCase
import com.asurspace.criminalintent.model.crimes.entities.Crime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCrimeVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val createCrimeUseCase: CreateCrimeUseCase
) : ViewModel(), LifecycleEventObserver {

    private val _uiState = MutableStateFlow {}
    val uiState = _uiState.asStateFlow()

    private val _titleLD = savedStateHandle.getLiveData<String>(CrimesTable.COLUMN_TITLE)
    val titleLD = _titleLD.share()

    private val _suspectLD = savedStateHandle.getLiveData<String>(CrimesTable.COLUMN_SUSPECT)
    val suspectLD = _suspectLD.share()

    private val _description = savedStateHandle.getLiveData<String>(CrimesTable.COLUMN_DESCRIPTION)
    val descriptionLD = _description.share()

    private val _imageUriLD = savedStateHandle.getLiveData<Uri>(CrimesTable.COLUMN_IMAGE_URI)
    val imageUriLD = _imageUriLD.share()

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
            _description.value = description
        }
    }

    fun setUpdatedImage(uri: Uri?) {
        if (imageUriLD.value != uri) {
            _imageUriLD.value = uri
        }
    }

    fun addCrime() {
        viewModelScope.launch {
            createCrimeUseCase(
                Crime(
                    id = null,
                    solved = false,
                    title = titleLD.value,
                    suspect = suspectLD.value,
                    description = descriptionLD.value,
                    creationDate = System.currentTimeMillis(),
                    imageURI = imageUriLD.value.toString()
                )
            )
        }
    }

    private fun initSSH() {
        if (!savedStateHandle.contains(CrimesTable.COLUMN_TITLE)
            || savedStateHandle.get<String>(CrimesTable.COLUMN_TITLE) != titleLD.value
        ) {
            savedStateHandle[CrimesTable.COLUMN_TITLE] = titleLD.value
        }

        if (!savedStateHandle.contains(CrimesTable.COLUMN_SUSPECT)
            || savedStateHandle.get<String>(CrimesTable.COLUMN_SUSPECT) != suspectLD.value
        ) {
            savedStateHandle[CrimesTable.COLUMN_SUSPECT] = suspectLD.value
        }

        if (!savedStateHandle.contains(CrimesTable.COLUMN_DESCRIPTION)
            || savedStateHandle.get<String>(CrimesTable.COLUMN_DESCRIPTION) != descriptionLD.value
        ) {
            savedStateHandle[CrimesTable.COLUMN_DESCRIPTION] = descriptionLD.value
        }

        if (!savedStateHandle.contains(CrimesTable.COLUMN_IMAGE_URI)
            || savedStateHandle.get<Uri>(CrimesTable.COLUMN_IMAGE_URI) != imageUriLD.value
        ) {
            savedStateHandle[CrimesTable.COLUMN_IMAGE_URI] = imageUriLD.value
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                Log.i("create", "CCVM")
            }
            Lifecycle.Event.ON_START -> {
                Log.i("start", "CCVM")

            }
            Lifecycle.Event.ON_RESUME -> {
                Log.i("resume", "CCVM")

            }
            Lifecycle.Event.ON_PAUSE -> {
                Log.i("pause", "CCVM")
            }
            Lifecycle.Event.ON_STOP -> {
                Log.i("stop", "CCVM")
            }
            Lifecycle.Event.ON_DESTROY -> {
                initSSH()
                Log.i("destroy", "CCVM")
            }
            Lifecycle.Event.ON_ANY -> {
            }
        }
    }

}