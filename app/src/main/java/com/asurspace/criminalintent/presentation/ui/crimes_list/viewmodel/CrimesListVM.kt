package com.asurspace.criminalintent.presentation.ui.crimes_list.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.common.utils.Event
import com.asurspace.criminalintent.common.utils.share
import com.asurspace.criminalintent.domain.usecase.get.GetCrimesListUseCase
import com.asurspace.criminalintent.domain.usecase.remove.RemoveCrimeUseCase
import com.asurspace.criminalintent.domain.usecase.update.SetSolvedUseCase
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import com.asurspace.criminalintent.presentation.ui.state.ErrorModel
import com.asurspace.criminalintent.presentation.ui.state.UIState
import com.asurspace.criminalintent.presentation.ui.state.getMutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CrimesListVM @Inject constructor(
    private val getCrimesListUseCase: GetCrimesListUseCase,
    private val removeCrimeUseCase: RemoveCrimeUseCase,
    private val setSolvedUseCase: SetSolvedUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), CrimesActionListener {

    private val _uiState = MutableStateFlow<UIState<MutableList<Crime>>>(UIState.Empty)
    val uiState = _uiState.asStateFlow()

    /*private val _uiState: MutableStateFlow<UIState<MutableList<Crime>>> = savedStateHandle.getMutableStateFlow("usState", UIState.Empty)
    val uiState = _uiState*/

    private val _moveToCrime = MutableLiveData<Event<Crime>>()
    val moveToItem = _moveToCrime.share()

    private val _openPreview = MutableLiveData<Event<String>>()
    val openPreview = _openPreview.share()

    private var crimeList = mutableListOf<Crime>()

    init {
        if (_uiState.value is UIState.Empty){
            loadCrimeList()
        }
        Log.d(TAG, crimeList.toString())
    }

    private fun loadCrimeList() {
        viewModelScope.launch {
            _uiState.value = UIState.Pending
            try {
                getCrimesListUseCase.invoke().collect{
                    _uiState.value = UIState.Success(it)
                }
            } catch (ex: IOException) {
                Log.d(TAG, ex.message.toString())
                onError(message = R.string.io_err)
            } catch (ex: Exception) {
                Log.d(TAG, ex.message.toString())
                onError(message = R.string.err)
            }
        }
        Log.d(TAG, crimeList.toString())
    }

    override fun onCrimeDelete(id: Long) {
        viewModelScope.launch {
            removeCrimeUseCase(id)
        }
    }

    override fun onStateChanged(id: Long, solved: Boolean, index: Int) {
        viewModelScope.launch {
            setSolvedUseCase(SetSolvedTuples(id, solved))
        }
        if (!crimeList.isNullOrEmpty()) {
            val crime = crimeList[index].copy(solved = solved)
            crimeList[index] = crime
        }
    }

    override fun onItemSelect(crime: Crime) {
        _moveToCrime.value = Event(crime)
    }

    override fun onImageClicked(image: String) {
        _openPreview.value = Event(image)
    }

    private fun onError(icon: Int = -1, title: Int = -1, message: Int) {
        _uiState.value = UIState.Error(
            error = ErrorModel(
                icon = icon,
                title = title,
                message = message
            )
        )
    }

    /*private fun saveToState(){
        savedStateHandle["usState"] = _uiState.asLiveData().value
    }*/

    override fun onCleared() {
        super.onCleared()
        //saveToState()
    }

    companion object {
        @JvmStatic
        private val TAG = "CrimesListVM"
    }

}