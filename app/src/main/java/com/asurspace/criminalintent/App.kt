package com.asurspace.criminalintent

import android.app.Application
import com.asurspace.criminalintent.model.crimes.CrimesService

class App: Application() {
    val crimesService = CrimesService(Repository.crimesRepo)
}