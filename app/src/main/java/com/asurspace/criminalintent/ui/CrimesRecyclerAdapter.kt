package com.asurspace.criminalintent.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asurspace.criminalintent.databinding.RecyclerCrimesItemBinding
import com.asurspace.criminalintent.model.crimes.entities.Crime

class CrimesRecyclerAdapter(
    private val crimes: List<Crime>,
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
        holder.setCrime(crimes[position])
    }

    override fun getItemCount(): Int = crimes.size

    inner class CrimeViewHolder(
        private val binding: RecyclerCrimesItemBinding,
        val selectedItem: (Crime) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private var crime: Crime? = null

        fun setCrime(crime: Crime) {
            this.crime = crime
            binding.rvSolvedCb.isChecked = (crime.solved ?: 0) == 1
            binding.crimeTitle.text = crime.title
            binding.suspectName.text = crime.suspectName
        }


        init {
            binding.rvSolvedCb.setOnCheckedChangeListener { _, b ->
                crime?.let {
                    //it.solved = b
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
    }
}