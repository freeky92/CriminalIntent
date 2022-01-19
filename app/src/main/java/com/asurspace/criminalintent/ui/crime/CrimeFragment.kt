package com.asurspace.criminalintent.ui.crime

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.asurspace.criminalintent.FragmentNameList
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.databinding.CrimeFragmentBinding
import com.asurspace.criminalintent.dateFormat
import com.asurspace.criminalintent.model.sqlite.AppSQLiteContract.CrimesTable
import java.util.*

class CrimeFragment : Fragment(R.layout.crime_fragment) {

    private val viewModel by viewModels<CrimeVM>()

    private var _binding: CrimeFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getCrimeIdFromList()
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

        restoreValue()
        listenerInitialization()
    }

    private fun restoreValue() {
        binding.updateTb.text = resources.getString(R.string.date)
            .plus(dateFormat.format(Date(viewModel.changedCrimeLD.value?.creation_date ?: 0)))
        binding.crimeTitleInput.editText?.setText(viewModel.changedCrimeLD.value?.title ?: "")
        binding.crimeSuspectNameInput.editText?.setText(
            viewModel.changedCrimeLD.value?.suspectName ?: ""
        )
        binding.crimeDescriptionInput.editText?.setText(
            viewModel.changedCrimeLD.value?.desciption ?: ""
        )
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

    private fun getCrimeIdFromList() {
        setFragmentResultListener(FragmentNameList.CRIME_FRAGMENT) { _, bundle ->
            // Any type can be passed via to the bundle
            val result = bundle.getLong(CrimesTable.COLUMN_ID)
            Log.i("bundle RESULT", result.toString())
            viewModel.setCrimeOnVM(result)
        }
    }

    override fun onResume() {
        super.onResume()
        fragmentResumeResult()
        getCrimeIdFromList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}