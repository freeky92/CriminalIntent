package com.asurspace.criminalintent.ui.crime

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.Repository
import com.asurspace.criminalintent.databinding.CrimeFragmentBinding
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.util.*
import com.asurspace.criminalintent.util.UtilPermissions.PERMISSIONS
import com.asurspace.criminalintent.util.UtilPermissions.hasPermissions
import com.asurspace.criminalintent.util.ui.PreviewFragment
import com.google.android.material.snackbar.Snackbar
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst.KEY_SELECTED_MEDIA
import droidninja.filepicker.FilePickerConst.REQUEST_CODE_PHOTO
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
class CrimeFragment : Fragment(R.layout.crime_fragment) {

    /*private val launchMPermissionsRequest =
        registerForActivityResult(RequestMultiplePermissions()) { permissions ->
            val neededList = emptyList<String>().toMutableList()
            permissions.entries.forEach {
                if (!it.value) {
                    neededList.add(it.key)
                }
            }
            if (neededList.isEmpty()) {
                openFilePicker()
            } else {
                (activity as MainActivity).showSnackBar("$neededList")
            }
        }*/

    private val permissionLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionResult
    )

    private val viewModel by viewModels<CrimeVM> { VMFactory(this, Repository.crimesRepo) }

    private var photoPaths = ArrayList<Any>()

    private var _binding: CrimeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CrimeFragmentBinding.inflate(inflater, container, false)

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

        binding.checkboxSolved.isChecked = viewModel.solvedLD.value ?: false

        if (!viewModel.titleLD.value.isNullOrEmpty()) {
            binding.crimeTitleInput.editText?.setText(viewModel.titleLD.value)
        }
        if (!viewModel.suspectLD.value.isNullOrEmpty()) {
            binding.crimeSuspectInput.editText?.setText(viewModel.suspectLD.value)
        }
        if (!viewModel.descriptionLD.value.isNullOrEmpty()) {
            binding.crimeDescriptionInput.editText?.setText(viewModel.descriptionLD.value)
        }
        binding.dateTv.text = resources.getString(R.string.creation_date_plus).plus(
            dateFormat.format(
                Date(viewModel.cDateLD.value ?: 0)
            )
        )
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
        binding.checkboxSolved.setOnCheckedChangeListener { _, b ->
            viewModel.setSolvedState(b)
        }

        binding.setImageButton.setOnClickListener {
            permissionLauncher.launch(PERMISSIONS)
        }



        binding.removeTb.setOnClickListener {
            //(activity as MainActivity)
            val snackBar = Snackbar.make(
                binding.root,
                "Are you sure?",
                Snackbar.LENGTH_INDEFINITE
            )
                .setBackgroundTint(Color.WHITE)
                .setTextColor(Color.BLACK)

            val sBarPar = snackBar.view.findViewById<AppCompatTextView>(R.id.snackbar_text)
            sBarPar?.let {
                it.textSize = 16f
                it.textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            snackBar.view.setOnClickListener {
                snackBar.dismiss()
            }
            snackBar.setAction("Delete") {
                viewModel.remove()
            }.show()
        }

        binding.crimeIv.setOnClickListener {
            if ((viewModel.imageUriLD.value ?: "").isNotEmpty()) {
                (activity as MainActivity).openFragment(PreviewFragment())
                setImageResult(viewModel.imageUriLD.value ?: "")
            }
        }

    }

    private fun subscribeOnLD() {
        viewModel.crimeLD.observe(viewLifecycleOwner) {
            viewModel.setFields()
            restoreValue()
            // TODO: 25.01.2022 - setFragmentResult
        }

        viewModel.imageUriLD.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.crimeIv.setImageURI(Uri.parse(it))
            }
        }

        viewModel.isRemoved.observe(viewLifecycleOwner) {
            with(activity as MainActivity) {
                showSnackBar(resources.getString(R.string.msg_crime_removed))
                onBackPressed()
            }
        }

    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                              // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to CRIME_FRAGMENT)
        )
    }

    private fun openFilePicker() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setActivityTheme(R.style.Theme_CriminalIntent)
            .pickPhoto(this)
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
        if (hasPermissions(requireContext(), *results.keys.toTypedArray())) {
            openFilePicker()
        } else { //false
            if (!shouldShowRequestPermissionRationale(results.keys.last())) {
                askForOpeningSettings()
            } else {
                (activity as MainActivity).showSnackBar("Permission needed!")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_PHOTO -> if (resultCode == Activity.RESULT_OK && data != null) {
                data.getParcelableArrayListExtra<Uri>(KEY_SELECTED_MEDIA)
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

    override fun onStart() {
        super.onStart()
        setFragmentResultListener(TO_CRIME_FRAGMENT) { _, b ->
            val result = b.get(CRIME)
            viewModel.setCrime(result as Crime)
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