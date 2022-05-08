package com.asurspace.criminalintent.domain.usecase.update

import com.asurspace.criminalintent.domain.repository.GetAddUpdateCrimeRepository
import com.asurspace.criminalintent.data.model.crimes.entities.Crime
import javax.inject.Inject

class UpdateCrimeUseCase @Inject constructor(
    private val getAddUpdateCrimeRepository: GetAddUpdateCrimeRepository
) {
    suspend operator fun invoke(crime: Crime?) {
        getAddUpdateCrimeRepository.updateCrime(crime)
    }

}