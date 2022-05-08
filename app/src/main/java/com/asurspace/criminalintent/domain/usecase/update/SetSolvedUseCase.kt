package com.asurspace.criminalintent.domain.usecase.update

import com.asurspace.criminalintent.domain.repository.GetAddUpdateCrimeRepository
import com.asurspace.criminalintent.data.model.crimes.room.entyties.SetSolvedTuples
import javax.inject.Inject

class SetSolvedUseCase @Inject constructor(
    private val getAddUpdateCrimeRepository: GetAddUpdateCrimeRepository
) {
    suspend operator fun invoke(statusTuples: SetSolvedTuples){
        getAddUpdateCrimeRepository.setSolved(statusTuples)
    }

}