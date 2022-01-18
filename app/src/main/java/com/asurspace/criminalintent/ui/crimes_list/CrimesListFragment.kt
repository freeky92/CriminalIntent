package com.asurspace.criminalintent.ui.crimes_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.asurspace.criminalintent.util.FragmentNameList
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.databinding.CrimesListFragmentBinding
import com.asurspace.criminalintent.ui.CrimesRecyclerAdapter
import com.asurspace.criminalintent.ui.crime.CrimeFragment

class CrimesListFragment : Fragment(R.layout.crimes_list_fragment) {

    private val viewModel by viewModels<CrimesListViewModel>()

    private var _binding: CrimesListFragmentBinding? = null

    private lateinit var crimesRecyclerAdapter: CrimesRecyclerAdapter

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

        listenerInitialization()
        subscribeOnLiveData()
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
        viewModel.crimeListLD.observe(viewLifecycleOwner, { crimes ->
            crimesRecyclerAdapter = CrimesRecyclerAdapter(crimes) { crime ->
                (activity as MainActivity).openFragment(CrimeFragment())
            }
            binding.crimeListRv.adapter = crimesRecyclerAdapter
        })
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
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to FragmentNameList.CRIMES_LIST_FRAGMENT)
        )
    }
}
