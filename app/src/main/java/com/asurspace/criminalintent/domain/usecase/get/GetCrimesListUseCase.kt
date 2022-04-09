package com.asurspace.criminalintent.domain.usecase.get

import com.asurspace.criminalintent.domain.repository.GetAddUpdateCrimeRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCrimesListUseCase @Inject constructor(
    private val getAddUpdateCrimeRepository: GetAddUpdateCrimeRepository
){
    operator fun invoke(): Flow<MutableList<Crime>> {
        return getAddUpdateCrimeRepository.getAllCrimes()
    }
}
