package com.asurspace.criminalintent.ui.crimes_list

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
import com.asurspace.criminalintent.util.UtilPermissions.hasPermissions
import com.asurspace.criminalintent.util.viewModelCreator
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class CrimesListFragment : Fragment(R.layout.crimes_list_fragment) {

    private val permissionLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionResult
    )

    private val viewModel by viewModelCreator { CrimesListVM(Repository.crimesRepo) }

    private lateinit var crimesAdapter: CrimesRecyclerAdapter

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

        permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
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
            crimesAdapter = CrimesRecyclerAdapter(crimes) { crime ->
                setCrimeToResult(crime)
                (activity as MainActivity).openFragment(CrimeFragment())
            }
            binding.crimeListRv.adapter = crimesAdapter
        }
    }

    private fun setCrimeToResult(crime: Crime) {
        setFragmentResult(
            TO_CRIME_FRAGMENT,
            bundleOf(CRIME to crime)
        )
    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                                   // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to CRIMES_LIST_FRAGMENT)
        )
    }

    private fun onGotPermissionResult(results: Map<String, Boolean>) {
        if (hasPermissions(requireContext(), *results.keys.toTypedArray())) {
            binding.permissionView.isVisible = false
            subscribeOnLiveData()
        } else {
            binding.permissionView.isVisible = true
            binding.repeatTb.setOnClickListener {
                permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
            askForOpeningSettings()
        }
    }

    private fun askForOpeningSettings() {
        val startSettingActivityIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context?.packageName, null)
        )
        if (context?.packageManager?.resolveActivity(
                startSettingActivityIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            (activity as MainActivity).showSnackBar("Permission denied forever!")
        } else {
            (activity as MainActivity).showDialog(
                "Our app will not works without this permission, you can add it in settings.",
                "Settings",
                startSettingActivityIntent
            )
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


}
