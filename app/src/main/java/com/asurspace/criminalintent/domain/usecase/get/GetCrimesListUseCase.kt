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

/*
operator fun invoke(): Flow<Resource<MutableList<Crime>>> = flow {
    try {
        val crimes = getAddUpdateCrimeRepository.getAllCrimes()
        emit(Resource.Success(crimes))
    } catch (e: IOException) {
        emit(
            Resource.Error(message = e.localizedMessage ?: "An unexpected error while getting data")
        )
    }
}*/
