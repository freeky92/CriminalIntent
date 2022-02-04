package com.asurspace.criminalintent.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.databinding.RecyclerCrimesItemBinding
import com.asurspace.criminalintent.model.crimes.CrimesRepository
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.entities.CrimeAdditional.emptyCrime
import com.asurspace.criminalintent.model.crimes.room.entyties.SetSolvedTuples
import kotlinx.coroutines.*


interface CrimesListener {

    fun onSelectCrime(crime: Crime)

    fun onCrimeDelete(id: Long)

    fun onStateChanged(id: Long, solved: Boolean)

}

@DelicateCoroutinesApi
class CrimesRecyclerAdapter(
    private var crimes: MutableList<Crime>?,
    private val selectedItem: (Crime) -> Unit,
) : RecyclerView.Adapter<CrimesRecyclerAdapter.CrimeViewHolder>() {

    private lateinit var binding: RecyclerCrimesItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        binding = RecyclerCrimesItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return CrimeViewHolder(binding, selectedItem)
    }

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        holder.setCrime(crimes?.get(position) ?: emptyCrime())
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
            binding.rvSolvedCb.isChecked = crime.solved ?: false
            binding.crimeTitle.text = crime.title
            binding.suspect.text = crime.suspect
            if (crime.imageURI != null) {
                binding.rvCrimeImage.setImageURI(Uri.parse(crime.imageURI))
            } else {
                binding.rvCrimeImage.setImageResource(R.drawable.ic_baseline_insert_photo_24)
            }

            binding.rvSolvedCb.setOnCheckedChangeListener { _, b ->
                crime.id?.let { changeState(it, b) }
            }

            binding.root.setOnClickListener {
                selectedItem(crime)
            }

            binding.popUpMenu.setOnClickListener {
                showPopUpMenu(it)
            }

        }

        @SuppressLint("NotifyDataSetChanged")
        private fun showPopUpMenu(view: View) {
            val popupMenu = PopupMenu(view.context, view)
            val context = view.context

            popupMenu.menu.add(
                0,
                ID_REMOVE,
                Menu.NONE,
                context.getString(R.string.remove_menu_item)
            )

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    ID_REMOVE -> {
                        removeItem(crime?.id ?: 0)
                        crimes?.remove(crime)
                        //crimes?.let { it1 -> notifyItemRemoved(it1.indexOf(crime)) }
                        notifyDataSetChanged()
                    }
                }
                return@setOnMenuItemClickListener true
            }

            popupMenu.show()
        }

        @DelicateCoroutinesApi
        private fun changeState(crimeId: Long, solved: Boolean) {
            GlobalScope.launch(Dispatchers.IO) {
                crimeDB.setSolved(SetSolvedTuples(crimeId, solved))
            }
        }

        private fun removeItem(crimeId: Long) {
            GlobalScope.launch(Dispatchers.IO) {
                crimeDB.deleteCrime(crimeId)
            }
        }


    }

    companion object {
        const val ID_REMOVE = 1
    }
}