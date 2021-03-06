package com.asurspace.criminalintent.presentation.ui.create_crime.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.asurspace.criminalintent.presentation.ui.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.common.utils.*
import com.asurspace.criminalintent.databinding.CreateCrimeFragmentBinding
import com.asurspace.criminalintent.navigation.ProviderCustomTitle
import com.asurspace.criminalintent.presentation.ui.preview.fragment.PreviewFragment
import com.asurspace.criminalintent.common.utils.UtilPermissions.PERMISSIONS
import com.asurspace.criminalintent.presentation.ui.create_crime.viewmodel.CreateCrimeVM
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CreateCrimeFragment : Fragment(R.layout.create_crime_fragment), ProviderCustomTitle {

    private val permissionLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionResult
    )
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument(), ::onGotImageResult)

    private var _binding: CreateCrimeFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<CreateCrimeVM>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateCrimeFragmentBinding.inflate(inflater, container, false)

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
        binding.dateTv.text = dateFormat.format(Date())

        if (!viewModel.titleLD.value.isNullOrEmpty()) {
            binding.crimeTitleInput.editText?.setText(viewModel.titleLD.value ?: "")
        }
        if (!viewModel.suspectLD.value.isNullOrEmpty()) {
            binding.crimeSuspectInput.editText?.setText(viewModel.suspectLD.value ?: "")
        }
        if (!viewModel.descriptionLD.value.isNullOrEmpty()) {
            binding.crimeDescriptionInput.editText?.setText(viewModel.descriptionLD.value ?: "")
        }
        if (viewModel.imageUriLD.value != null) {
            binding.crimeIv.setImageURI(viewModel.imageUriLD.value)
        }

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

        binding.setImageButton.setOnClickListener {
            permissionLauncher.launch(PERMISSIONS)
        }

        binding.createTb.setOnClickListener {
            viewModel.addCrime()
            with(activity as MainActivity) {
                showSnackBar(resources.getString(R.string.msg_crime_created))
                onBackPressed()
            }
        }
        with(binding.crimeIv) {
            setOnClickListener {
                if (viewModel.imageUriLD.value != null) {
                    (activity as MainActivity).openFragment(PreviewFragment())
                    setImageResult(viewModel.imageUriLD.value.toString())
                }
            }
            setOnLongClickListener {
                viewModel.setUpdatedImage(Uri.EMPTY)
                true
            }
        }


    }

    private fun subscribeOnLD() {
        viewModel.imageUriLD.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.crimeIv.setImageURI(it)
            }
        }
    }

    private fun setImageResult(uri: String) {
        setFragmentResult(
            PREVIEW,
            bundleOf(IMAGE to (uri))
        )
    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                              // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to CREATE_CRIME_FRAGMENT)
        )
    }

    private fun askForOpeningSettings() {
        val startSettingActivityIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context?.packageName, null)
        )
        if (context?.packageManager?.resolveActivity(
                startSettingActivityIntent, PackageManager.MATCH_DEFAULT_ONLY
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

    private fun onGotPermissionResult(results: Map<String, Boolean>) {
        if (UtilPermissions.hasPermissions(requireContext(), *results.keys.toTypedArray())) {
            pickImageLauncher.launch(arrayOf("image/*"))
        } else {
            if (!shouldShowRequestPermissionRationale(results.keys.last())) {
                askForOpeningSettings()
            } else {
                (activity as MainActivity).showSnackBar("Permission needed!")
            }
        }
    }

    private fun onGotImageResult(uri: Uri?) {
        uri?.let {
            context?.contentResolver?.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.setUpdatedImage(uri)
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

    override fun getTitle() = R.string.create_crime

}
