package com.asurspace.criminalintent.ui.create_crime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asurspace.criminalintent.FragmentNameList
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.databinding.CreateCrimeFragmentBinding

class CreateCrimeFragment : Fragment(R.layout.create_crime_fragment) {

    private val viewModel by viewModels<CreateCrimeVM>()

    private var _binding: CreateCrimeFragmentBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateCrimeFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        restoreValue()
        listenerInitialization()
    }

    private fun restoreValue() {
        binding.crimeTitleInput.editText?.setText(viewModel.titleLD.value ?: "")
        binding.crimeSuspectNameInput.editText?.setText(viewModel.suspectLD.value ?: "")
        binding.crimeDescriptionInput.editText?.setText(viewModel.descriptionLD.value ?: "")
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

        binding.createPlusDateTb.setOnClickListener {
            viewModel.addCrime()
            (activity as MainActivity).showSnackBar(resources.getString(R.string.msg_crime_created))
        }


    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                              // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to FragmentNameList.CREATE_CRIME_FRAGMENT)
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
