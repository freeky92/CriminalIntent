package com.asurspace.criminalintent.ui.crime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asurspace.criminalintent.util.FragmentNameList
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.util.ProjectConstant.dateFormat
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.databinding.CrimeFragmentBinding
import java.util.*

class CrimeFragment() : Fragment(R.layout.crime_fragment) {

    private val viewModel by viewModels<CrimeViewModel> { CrimeVMFactory(this) }

    private var _binding: CrimeFragmentBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CrimeFragmentBinding.inflate(inflater, container, false)

        binding.dateTb.text = dateFormat.format(Date())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkboxSolved.setOnCheckedChangeListener { _, b ->
            viewModel.setSolvedState(b)
        }


    }

    // todo

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                                       // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
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