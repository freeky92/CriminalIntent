package com.asurspace.criminalintent.ui.crime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.databinding.CrimeFragmentBinding
import com.asurspace.criminalintent.model.SharedVM
import com.asurspace.criminalintent.util.FragmentNameList
import com.asurspace.criminalintent.util.dateFormat
import com.asurspace.criminalintent.util.viewModelCreator
import java.util.*

class CrimeFragment : Fragment(R.layout.crime_fragment) {

    private val sharedViewModel by activityViewModels<SharedVM>()
    private val viewModel by viewModelCreator {
        CrimeVM(
            sharedViewModel.crimeId.value ?: 0,
            Repository.crimesRepo
        )
    }

    private var _binding: CrimeFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel.setCrimeOnVM(sharedViewModel.crimeId.value ?: 0)
        /*setFragmentResultListener(FragmentNameList.CRIME_FRAGMENT) { _, bundle ->
            viewModel.setCrimeOnVM(bundle.getLong(CrimesTable.COLUMN_ID))
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CrimeFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(viewModel)

        restoreValue()
        listenerInitialization()
    }

    private fun restoreValue() {
        viewModel.crimeLD.observe(viewLifecycleOwner) {
            binding.crimeTitleInput.editText?.setText(it?.title)
            binding.crimeSuspectNameInput.editText?.setText(it?.suspect)
            binding.crimeDescriptionInput.editText?.setText(it?.desciption)
            binding.checkboxSolved.isChecked = it?.solved == 1
            binding.buttonTitleTv1.text = resources.getString(R.string.details_plus).plus(
                dateFormat.format(
                    Date(it?.creation_date ?: 0)
                )
            )
            binding.updateTb.text = resources.getString(R.string.update)
        }

    }

    private fun listenerInitialization() {

        binding.crimeTitleInput.editText?.addTextChangedListener {
            viewModel.setUpdatedTitle(it.toString())
        }
        binding.crimeSuspectNameInput.editText?.addTextChangedListener {
            viewModel.setUpdatedSuspect(it.toString())
        }
        binding.crimeDescriptionInput.editText?.addTextChangedListener {
            viewModel.setUpdatedDescription(it.toString())
        }

        binding.checkboxSolved.setOnCheckedChangeListener { _, b ->
            viewModel.setSolvedState(b)
        }

        binding.updateTb.setOnClickListener {
            viewModel.update()
            (activity as MainActivity).showSnackBar(resources.getString(R.string.msg_crime_created))
        }

    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                              // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to FragmentNameList.CRIME_FRAGMENT)
        )
    }

    override fun onResume() {
        super.onResume()
        fragmentResumeResult()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}