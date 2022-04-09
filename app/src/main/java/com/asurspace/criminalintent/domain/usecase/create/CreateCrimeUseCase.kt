package com.asurspace.criminalintent.domain.usecase.create

import com.asurspace.criminalintent.domain.repository.GetAddUpdateCrimeRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import javax.inject.Inject

class CreateCrimeUseCase @Inject constructor(
    private val getAddUpdateCrimeRepository: GetAddUpdateCrimeRepository
) {
    suspend operator fun invoke(crime: Crime) {
        getAddUpdateCrimeRepository.addCrime(crime)
    }
}