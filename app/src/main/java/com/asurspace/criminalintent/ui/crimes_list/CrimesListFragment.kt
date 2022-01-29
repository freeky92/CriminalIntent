package com.asurspace.criminalintent.ui.crimes_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.databinding.CrimesListFragmentBinding
import com.asurspace.criminalintent.model.SharedVM
import com.asurspace.criminalintent.model.sqlite.AppSQLiteContract
import com.asurspace.criminalintent.ui.CrimesRecyclerAdapter
import com.asurspace.criminalintent.ui.crime.CrimeFragment
import com.asurspace.criminalintent.util.FragmentNameList
import com.asurspace.criminalintent.util.viewModelCreator
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class CrimesListFragment : Fragment(R.layout.crimes_list_fragment) {

    private val sharedViewModel by activityViewModels<SharedVM>()
    private val viewModel by viewModelCreator { CrimesListVM(Repository.crimesRepo) }
    private lateinit var crimesRecyclerAdapter: CrimesRecyclerAdapter

    private var _binding: CrimesListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setFragmentResultListener() todo

    }

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
        viewModel.crimeListLD.observe(viewLifecycleOwner) { crimes ->
            crimesRecyclerAdapter = CrimesRecyclerAdapter(crimes) { crime ->
                sharedViewModel.setCrimeId(crime.id)
                (activity as MainActivity).openFragment(CrimeFragment())
            }
            binding.crimeListRv.adapter = crimesRecyclerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        fragmentResumeResult()
    }

    override fun onPause() {
        super.onPause()
        Log.i("onPause", "")
    }

    override fun onStop() {
        super.onStop()
        Log.i("onStop", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("onDESTROY", "")
        _binding = null
    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                                   // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to FragmentNameList.CRIMES_LIST_FRAGMENT)
        )
    }

    private fun setResult(crimeId: Long?) {
        setFragmentResult(
            FragmentNameList.CRIME_FRAGMENT,
            bundleOf(AppSQLiteContract.CrimesTable.COLUMN_ID to crimeId)
        )
    }

}
