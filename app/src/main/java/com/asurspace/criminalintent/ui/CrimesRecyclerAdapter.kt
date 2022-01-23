package com.asurspace.criminalintent.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.databinding.RecyclerCrimesItemBinding
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class CrimesRecyclerAdapter(
    private val crimes: List<Crime>?,
    private val selectedItem: (Crime) -> Unit,
) :
    RecyclerView.Adapter<CrimesRecyclerAdapter.CrimeViewHolder>() {

    private lateinit var binding: RecyclerCrimesItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        binding =
            RecyclerCrimesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CrimeViewHolder(binding, selectedItem)
    }

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        holder.setCrime(crimes?.get(position) ?: Crime(null, null, null, null, null, null, null))
    }

    override fun getItemCount(): Int = crimes?.size ?: 0

    @DelicateCoroutinesApi
    inner class CrimeViewHolder(
        private val binding: RecyclerCrimesItemBinding,
        val selectedItem: (Crime) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val crimeDB: CrimesRepository = Repository.crimesRepo

        private var crime: Crime? = null

        fun setCrime(crime: Crime) {
            this.crime = crime
            binding.rvSolvedCb.isChecked = (crime.solved ?: 0) == 1
            binding.crimeTitle.text = crime.title
            binding.suspectName.text = crime.suspect
        }


        init {
            binding.rvSolvedCb.setOnCheckedChangeListener { _, b ->
                crime?.let {
                    //TODO("#2")
                    changeState(b, it)
                }
            }

            binding.ll.setOnClickListener {
                crime?.let {
                    selectedItem(it)
                }
            }
            binding.rvCrimeImage.setOnClickListener {
                crime?.let {
                    selectedItem(it)
                }
            }
        }

        @DelicateCoroutinesApi
        private fun changeState(solved: Boolean, crime: Crime) {
            val c = crime.toMutableCrime()

            if (solved) {
                c.solved = 1
            } else {
                c.solved = 0
            }

            GlobalScope.launch(Dispatchers.IO) {
                crimeDB.updateCrime(crime.id, c.toCrime())
            }
        }

    }
}