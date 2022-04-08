package com.asurspace.criminalintent.presentation.ui.crimes_list.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.common.utils.Event
import com.asurspace.criminalintent.common.utils.share
import com.asurspace.criminalintent.domain.repository.CrimesListRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import com.asurspace.criminalintent.presentation.common.CrimesActionListener
import com.asurspace.criminalintent.presentation.ui.state.UIState
import com.example.frequency.foundation.model.state.ErrorModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CrimesListVM @Inject constructor(
    private val crimesListRepository: CrimesListRepository
) : ViewModel(), CrimesActionListener {

    private val _uiState = MutableStateFlow<UIState<MutableList<Crime>>>(UIState.Empty)
    val uiState = _uiState.asStateFlow()

    private val _moveToCrime = MutableLiveData<Event<Crime>>()
    val moveToItem = _moveToCrime.share()

    private val _openPreview = MutableLiveData<Event<String>>()
    val openPreview = _openPreview.share()

    private var crimeList = mutableListOf<Crime>()

    init {
        if (crimeList.isEmpty()) {
            loadCrimeList()
        }
        Log.d(TAG, crimeList.toString())
    }

    private fun loadCrimeList() {
        viewModelScope.launch {
            _uiState.value = UIState.Pending
            try {
                crimeList
                crimesListRepository.getAllCrimes(false).collect() {
                    Log.d(TAG, crimeList.toString())
                    _uiState.value = UIState.Success(it)
                }
                /*flow<Crime> {
                    crimeRepository.getAllCrimes(false).collect{

                    }
                }

                _uiState.value = UIState.Success()*/

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
            crimesListRepository.deleteCrime(id)
        }
    }

    override fun onStateChanged(id: Long, solved: Boolean, index: Int) {
        viewModelScope.launch {
            crimesListRepository.setSolved(SetSolvedTuples(id, solved))
        }
        if (!crimeList.isNullOrEmpty()) {
            val list = crimeList
            val crime = list[index].copy(solved = solved)
            list[index] = crime
            crimeList = list
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

    companion object {
        @JvmStatic
        private val TAG = "CrimesListVM"
    }

}