package com.asurspace.criminalintent.model.crimes

import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.entities.CrimeAdditional.emptyCrime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias CrimesListener = (crimes: List<Crime>) -> Unit

class CrimesService(private val crimesDB: CrimesRepository) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var crimes = mutableListOf<Crime>()
    private var currentCrime = emptyCrime()
    private var loaded = false

    private val listeners = mutableSetOf<CrimesListener>()

    init {
        if (crimes.isNullOrEmpty()) {
            loadCrimes()
        }
    }

    fun loadCrimes() {
        scope.launch {
            val updatedListC =
                crimesDB.getAllCrimes(false)?.toMutableList() ?: emptyList<Crime>().toMutableList()
            if (updatedListC != crimes) {
                crimes = updatedListC
                loaded = true
                notifyChanges()
            }
        }
    }

    fun getById(id: Long) {
        scope.launch {
            currentCrime = crimesDB.findCrimeById(id) ?: emptyCrime()
        }
    }

    fun removeCrime(id: Long) {
        scope.launch {
            crimesDB.deleteCrime(id)
            delay(50)
            notifyChanges()
        }
    }

    fun addListener(listener: CrimesListener) {
        listeners.add(listener)
        if (loaded) {
            listener.invoke(crimes)
        }
    }

    fun removeListener(listener: CrimesListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        if (!loaded) return
        listeners.forEach { it.invoke(crimes) }
    }

}