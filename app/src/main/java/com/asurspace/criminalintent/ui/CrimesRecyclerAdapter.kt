package com.asurspace.criminalintent.ui

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asurspace.criminalintent.R
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
) : RecyclerView.Adapter<CrimesRecyclerAdapter.CrimeViewHolder>() {

    var crimeList: List<Crime> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

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
            binding.suspect.text = crime.suspect
            if (crime.imageURI != null) {
                binding.rvCrimeImage.setImageURI(Uri.parse(crime.imageURI))
            } else {
                binding.rvCrimeImage.setImageResource(R.drawable.ic_baseline_insert_photo_24)
            }

            binding.rvSolvedCb.setOnCheckedChangeListener { _, b ->
                changeState(b, crime)
            }

            binding.layerGroup.setOnClickListener {
                selectedItem(crime)
            }

            binding.layerGroup.setOnLongClickListener {
                removeItem(crime.id ?: 0)
                true
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
            Log.i("changeState", "$c $solved")
            GlobalScope.launch(Dispatchers.IO) {
                crimeDB.updateCrime(crime.id, c.toCrime())
            }
        }

        private fun removeItem(crimeId: Long) {
            GlobalScope.launch(Dispatchers.IO) {
                crimeDB.deleteCrime(crimeId)
            }
        }


    }
}