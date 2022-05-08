package com.asurspace.criminalintent.domain.usecase.remove

import com.asurspace.criminalintent.domain.repository.RemoveCrimesRepository
import javax.inject.Inject

class RemoveCrimeUseCase  @Inject constructor(
    private val removeCrimesRepository: RemoveCrimesRepository
){
    suspend operator fun invoke(crimeId: Long): Int = removeCrimesRepository.deleteCrime(crimeId)
}