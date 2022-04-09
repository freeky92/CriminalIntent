package com.asurspace.criminalintent.presentation.ui.crimes_list.fragment

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.common.utils.*
import com.asurspace.criminalintent.common.utils.UtilPermissions.hasPermissions
import com.asurspace.criminalintent.databinding.CrimesListFragmentBinding
import com.asurspace.criminalintent.navigation.ProviderCustomTitle
import com.asurspace.criminalintent.presentation.common.CrimesRecyclerAdapter
import com.asurspace.criminalintent.presentation.ui.MainActivity
import com.asurspace.criminalintent.presentation.ui.crime.fragment.EditCrimeFragment
import com.asurspace.criminalintent.presentation.ui.crimes_list.viewmodel.CrimesListVM
import com.asurspace.criminalintent.presentation.ui.preview.fragment.PreviewFragment
import com.asurspace.criminalintent.presentation.ui.state.UIState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrimesListFragment : Fragment(R.layout.crimes_list_fragment), ProviderCustomTitle {

    private val permissionLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionResult
    )

    private val viewModel by viewModels<CrimesListVM>()

    private lateinit var crimesAdapter: CrimesRecyclerAdapter

    private var _binding: CrimesListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CrimesListFragmentBinding.inflate(inflater, container, false)

        crimesAdapter = CrimesRecyclerAdapter(viewModel)
        subscribeOnLiveData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenerInitialization()
        permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
    }

    private fun listenerInitialization() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true).apply {
                stackFromEnd = true
            }
        binding.crimeListRv.run {
            this.layoutManager = layoutManager
            this.adapter = crimesAdapter
        }
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            uiState.asLiveData().observe(viewLifecycleOwner) { state ->
                binding.progressBar.visibility = View.GONE
                when (state) {
                    is UIState.Empty -> {

                    }
                    is UIState.Pending -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is UIState.Error -> {

                    }
                    is UIState.Success -> {
                        crimesAdapter.crimes = state.data
                    }
                }
            }
            moveToItem.observe(viewLifecycleOwner) { event ->
                event.get()?.let { crime ->
                    setCrimeToResult(crime, TO_CRIME_FRAGMENT)
                    (requireActivity() as MainActivity).openFragment(EditCrimeFragment())
                }
            }
            openPreview.observe(viewLifecycleOwner) { event ->
                event.get()?.let { uri ->
                    setCrimeToResult(uri, PREVIEW)
                    (activity as MainActivity).openFragment(PreviewFragment())
                }
            }
        }

    }

    private fun setCrimeToResult(obj: Any, destination: String) {
        val name = if (destination == TO_CRIME_FRAGMENT) CRIME else IMAGE
        setFragmentResult(
            destination,
            bundleOf(name to obj)
        )
    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,
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

    override fun getTitle() = R.string.crimes_list

}
