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
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
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
        subscribeOnLD()
    }

    private fun restoreValue() {

        binding.checkboxSolved.isChecked = viewModel.solvedLD.value == 1

        binding.crimeTitleInput.editText?.setText(viewModel.titleLD.value)

        binding.crimeSuspectInput.editText?.setText(viewModel.suspectLD.value)

        binding.crimeDescriptionInput.editText?.setText(viewModel.descriptionLD.value)

        binding.dateTv.text = resources.getString(R.string.creation_date_plus).plus(
            dateFormat.format(
                Date(viewModel.cDateLD.value ?: 0)
            )
        )

    }

    private fun listenerInitialization() {

        binding.crimeTitleInput.editText?.addTextChangedListener {
            viewModel.setUpdatedTitle(it.toString())
        }
        binding.crimeSuspectInput.editText?.addTextChangedListener {
            viewModel.setUpdatedSuspect(it.toString())
        }
        binding.crimeDescriptionInput.editText?.addTextChangedListener {
            viewModel.setUpdatedDescription(it.toString())
        }

        binding.checkboxSolved.setOnCheckedChangeListener { _, b ->
            viewModel.setSolvedState(b)
        }

        binding.removeTb.setOnClickListener {
            viewModel.remove()
            with(activity as MainActivity) {
                showSnackBar(resources.getString(R.string.msg_crime_removed))
                onBackPressed()
            }
        }

    }

    private fun subscribeOnLD() {
        viewModel.crimeLD.observe(viewLifecycleOwner) {
            viewModel.setFields()
            restoreValue()
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