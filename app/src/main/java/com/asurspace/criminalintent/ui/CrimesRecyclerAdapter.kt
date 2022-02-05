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
import com.asurspace.criminalintent.databinding.RecyclerCrimesItemBinding
import com.asurspace.criminalintent.model.crimes.entities.Crime


interface CrimesActionListener {

    fun onCrimeDelete(id: Long)

    fun onStateChanged(id: Long, solved: Boolean)

    fun updateList(crimesList: MutableList<Crime>?)

}

class CrimesRecyclerAdapter(
    private val actionActionListener: CrimesActionListener,
    crimesList: MutableList<Crime>?,
    private val selectedItem: (Crime) -> Unit,
) : RecyclerView.Adapter<CrimesRecyclerAdapter.CrimeViewHolder>() {

    var crimes: MutableList<Crime> = crimesList ?: mutableListOf()
        @SuppressLint("NotifyDataSetChanged")
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        val binding: RecyclerCrimesItemBinding = RecyclerCrimesItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return CrimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        val crime = crimes[position]

        with(holder.binding) {
            holder.itemView.tag = crime
            popUpMenu.tag = crime


            rvSolvedCb.isChecked = crime.solved ?: false
            crimeTitle.text = crime.title
            suspect.text = crime.suspect
            if (crime.imageURI != null) {
                rvCrimeImage.setImageURI(Uri.parse(crime.imageURI))
            } else {
                rvCrimeImage.setImageResource(R.drawable.ic_baseline_insert_photo_24)
            }

            rvSolvedCb.setOnCheckedChangeListener { _, b ->
                val index = crimes.indexOfFirst { it.id == crime.id }
                crime.id?.let {
                    actionActionListener.onStateChanged(it, b)
                    val newCrime = crime.toMutableCrime()
                    newCrime.solved = b
                    crimes[index] = newCrime.toCrime()
                    actionActionListener.updateList(crimes)
                }
            }

            root.setOnClickListener {
                selectedItem(crime)
            }

            popUpMenu.setOnClickListener {
                showPopUpMenu(it)
            }
        }
    }

    override fun getItemCount(): Int = crimes.size

    @SuppressLint("NotifyDataSetChanged")
    private fun showPopUpMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val crime = view.tag as Crime

        popupMenu.menu.add(
            0,
            ID_REMOVE,
            Menu.NONE,
            context.getString(R.string.remove_menu_item)
        )

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_REMOVE -> {
                    actionActionListener.onCrimeDelete(crime.id ?: 0)
                    crimes.remove(crime)
                    notifyDataSetChanged()
                }
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    inner class CrimeViewHolder(
        val binding: RecyclerCrimesItemBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val ID_REMOVE = 1
    }
}