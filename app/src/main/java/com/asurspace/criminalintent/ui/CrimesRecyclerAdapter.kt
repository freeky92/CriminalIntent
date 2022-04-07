package com.asurspace.criminalintent.ui

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
import com.bumptech.glide.Glide


interface CrimesActionListener {

    fun onCrimeDelete(id: Long)

    fun onStateChanged(id: Long, solved: Boolean, index: Int)

    fun onItemSelect(crime: Crime)

}

class CrimesRecyclerAdapter(
    private val actionListener: CrimesActionListener,
) : RecyclerView.Adapter<CrimesRecyclerAdapter.CrimeViewHolder>() {

    var crimes: List<Crime> = emptyList()
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

        holder.setIsRecyclable(false)

        with(holder.binding) {
            holder.itemView.tag = crime
            popUpMenu.tag = crime


            rvSolvedCb.isChecked = crime.solved ?: false
            crimeTitle.text = crime.title
            suspect.text = crime.suspect
            if (!crime.imageURI.isNullOrBlank()) {
                Glide.with(root.context).load(Uri.parse(crime.imageURI))
                    .placeholder(R.drawable.ic_baseline_insert_photo_24)
                    .error(R.drawable.ic_baseline_insert_photo_24)
                    .into(rvCrimeImage)
            }

            rvSolvedCb.setOnCheckedChangeListener { _, b ->
                val index = crimes.indexOf(crime)
                crime.id?.let {
                    actionListener.onStateChanged(it, b, index)
                }
                notifyItemChanged(index)
            }

            root.setOnClickListener {
                actionListener.onItemSelect(crime)
            }

            popUpMenu.setOnClickListener {
                showPopUpMenu(it)
            }
        }
    }

    override fun getItemCount(): Int = crimes.size

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
                    actionListener.onCrimeDelete(crime.id ?: 0)
                    notifyItemRemoved(crimes.indexOf(crime))
                }
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    inner class CrimeViewHolder(
        val binding: RecyclerCrimesItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {


    }

    companion object {
        const val ID_REMOVE = 1
    }
}