package com.asurspace.criminalintent.presentation.common

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.databinding.RecyclerCrimesItemBinding
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.presentation.ui.crimes_list.viewmodel.CrimesActionListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class CrimesRecyclerAdapter(
    private val actionListener: CrimesActionListener,
) : RecyclerView.Adapter<CrimesRecyclerAdapter.CrimeViewHolder>() {

    var crimes: MutableList<Crime> = mutableListOf()
        set(newValue) {
            val diffCallback = CrimesDiffCallback(field, newValue)
            val diffUtilResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffUtilResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        val binding: RecyclerCrimesItemBinding =
            RecyclerCrimesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CrimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        val crime = crimes[position]

        holder.binding.apply {
            popUpMenu.tag = crime

            rvSolvedCb.isChecked = crime.solved!!
            crimeTitle.text = crime.title
            suspect.text = crime.suspect

            root.setOnClickListener {
                actionListener.onItemSelect(crime)
            }

            rvSolvedCb.setOnCheckedChangeListener { _, b ->
                crime.id?.let {
                    val index = crimes.findIndexById(crime.id)
                    actionListener.onStateChanged(it, b, index)
                }
            }

            if (!crime.imageURI.isNullOrBlank() && crime.imageURI != "null") {
                Log.d(TAG, crimes.size.toString())

                        Log.d(TAG, "\nlayoutPosition:${holder.layoutPosition}\n" +
                        "oldPosition:${holder.oldPosition}\nbindingPosition:${holder.bindingAdapterPosition}\n" +
                        "absoluteAP:${holder.absoluteAdapterPosition}\nposition:$position ${crime.imageURI}")
                Glide.with(root.context).load(Uri.parse(crime.imageURI))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(rvCrimeImage)
                rvCrimeImage.setOnClickListener {
                    actionListener.onImageClicked(crime.imageURI)
                }
            }

            popUpMenu.setOnClickListener {
                showPopUpMenu(it)
            }

        }
    }

    override fun getItemCount(): Int = crimes.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun showPopUpMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val crime = view.tag as Crime
        val index = crimes.findIndexById(crime.id!!)

        popupMenu.menu.add(
            0,
            ID_REMOVE,
            Menu.NONE,
            context.getString(R.string.remove_menu_item)
        )

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_REMOVE -> {
                    actionListener.onCrimeDelete(crime.id)
                    crimes.remove(crime)
                    notifyItemRemoved(index)
                }
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    private fun List<Crime>.findIndexById(userId: Long): Int = this.indexOfFirst { it.id == userId }

    inner class CrimeViewHolder(
        val binding: RecyclerCrimesItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val ID_REMOVE = 1

        @JvmStatic
        private val TAG = "CrimesRecyclerAdapter"
    }

}