package com.asurspace.criminalintent.ui.crimes_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.asurspace.criminalintent.util.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class CrimesListFragment : Fragment(R.layout.crimes_list_fragment) {

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

        if (!UtilPermissions.hasPermissions(requireContext(), *UtilPermissions.PERMISSIONS)) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                UtilPermissions.PERMISSIONS,
                UtilPermissions.PERMISSION_ALL
            )
        } else {
            subscribeOnLiveData()
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
                setResult(crime)
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

    private fun setResult(crime: Crime) {
        setFragmentResult(
            TO_CRIME_FRAGMENT,
            bundleOf(CRIME to crime)
        )
    }

}
