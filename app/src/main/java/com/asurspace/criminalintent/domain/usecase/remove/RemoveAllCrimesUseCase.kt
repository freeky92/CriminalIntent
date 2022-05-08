package com.asurspace.criminalintent.domain.usecase.remove

import com.asurspace.criminalintent.domain.repository.RemoveCrimesRepository
import javax.inject.Inject

class RemoveAllCrimesUseCase @Inject constructor(
    private val removeCrimesRepository: RemoveCrimesRepository
){
    operator fun invoke(){

    }

}