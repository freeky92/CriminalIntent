package com.asurspace.criminalintent.presentation.ui.crimes_list.viewmodel

import com.asurspace.criminalintent.data.model.crimes.entities.Crime

interface CrimesActionListener {

    fun onCrimeDelete(id: Long)

    fun onStateChanged(id: Long, solved: Boolean, index: Int)

    fun onItemSelect(crime: Crime)

    fun onImageClicked(image: String)

}