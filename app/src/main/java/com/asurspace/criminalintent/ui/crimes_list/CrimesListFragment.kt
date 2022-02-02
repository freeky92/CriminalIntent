package com.asurspace.criminalintent.ui.crimes_list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.databinding.CrimesListFragmentBinding
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.ui.CrimesRecyclerAdapter
import com.asurspace.criminalintent.ui.crime.CrimeFragment
import com.asurspace.criminalintent.util.CRIME
import com.asurspace.criminalintent.util.CRIMES_LIST_FRAGMENT
import com.asurspace.criminalintent.util.TO_CRIME_FRAGMENT
import com.asurspace.criminalintent.util.viewModelCreator
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class CrimesListFragment : Fragment(R.layout.crimes_list_fragment) {

    private val launchSinglePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                subscribeOnLiveData()
            } else {
                (activity as MainActivity).showSnackBar("STORAGE permission needed!")
            }
        }

    private val viewModel by viewModelCreator { CrimesListVM(Repository.crimesRepo) }

    private lateinit var crimesRecyclerAdapter: CrimesRecyclerAdapter

    private var _binding: CrimesListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CrimesListFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(viewModel)

        listenerInitialization()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            subscribeOnLiveData()
        } else {
            launchSinglePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }


    private fun listenerInitialization() {
        binding.crimeListRv.run {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true).apply {
                    stackFromEnd = true
                }
        }
    }

    private fun subscribeOnLiveData() {
        viewModel.crimeListLD.observe(viewLifecycleOwner) { crimes ->
            crimesRecyclerAdapter = CrimesRecyclerAdapter(crimes) { crime ->
                setCrimeToResult(crime)
                (activity as MainActivity).openFragment(CrimeFragment())
            }
            binding.crimeListRv.adapter = crimesRecyclerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        fragmentResumeResult()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                                   // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to CRIMES_LIST_FRAGMENT)
        )
    }

    private fun setCrimeToResult(crime: Crime) {
        setFragmentResult(
            TO_CRIME_FRAGMENT,
            bundleOf(CRIME to crime)
        )
    }

}
