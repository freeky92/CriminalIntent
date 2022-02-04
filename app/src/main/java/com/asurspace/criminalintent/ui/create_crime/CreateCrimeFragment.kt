package com.asurspace.criminalintent.ui.create_crime

import android.app.Activity
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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.databinding.CreateCrimeFragmentBinding
import com.asurspace.criminalintent.util.*
import com.asurspace.criminalintent.util.UtilPermissions.PERMISSIONS
import com.asurspace.criminalintent.util.ui.PreviewFragment
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.FilePickerConst.REQUEST_CODE_PHOTO
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
class CreateCrimeFragment : Fragment(R.layout.create_crime_fragment) {

    private val permissionLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionResult
    )

    private val viewModel by viewModels<CreateCrimeVM>()

    private var photoPaths = ArrayList<Any>()

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
        if (!viewModel.imageUriLD.value.isNullOrEmpty()) {
            binding.crimeIv.setImageURI(Uri.parse(viewModel.imageUriLD.value))
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

        binding.crimeIv.setOnClickListener {
            if ((viewModel.imageUriLD.value ?: "").isNotEmpty()) {
                (activity as MainActivity).openFragment(PreviewFragment())
                setImageResult(viewModel.imageUriLD.value ?: "")
            }
        }

    }

    private fun subscribeOnLD() {
        viewModel.imageUriLD.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.crimeIv.setImageURI(Uri.parse(it))
            }
        }
    }

    private fun openFilePicker() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setActivityTheme(R.style.Theme_CriminalIntent)
            .pickPhoto(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_PHOTO -> if (resultCode == Activity.RESULT_OK && data != null) {
                data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
                    ?.let { photoPaths.addAll(it) }
            }
        }
        if (photoPaths.isNotEmpty()) {
            viewModel.setUpdatedImage((photoPaths.first() as Uri).toString())
            photoPaths.clear()
        }
    }

    private fun setImageResult(uri: String) {
        setFragmentResult(
            PREVIEW,
            bundleOf(IMAGE to (uri))
        )
    }

    private fun onGotPermissionResult(results: Map<String, Boolean>) {
        if (UtilPermissions.hasPermissions(requireContext(), *results.keys.toTypedArray())) {
            openFilePicker()
        } else { //false
            if (!shouldShowRequestPermissionRationale(results.keys.last())) {
                askForOpeningSettings()
            } else {
                (activity as MainActivity).showSnackBar("Permission needed!")
            }
        }
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


    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                              // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to CREATE_CRIME_FRAGMENT)
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
