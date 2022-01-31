package com.asurspace.criminalintent.ui.crime

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.databinding.CrimeFragmentBinding
import com.asurspace.criminalintent.model.SharedVM
import com.asurspace.criminalintent.util.FragmentNameList
import com.asurspace.criminalintent.util.IMAGE
import com.asurspace.criminalintent.util.PREVIEW
import com.asurspace.criminalintent.util.UtilPermissions.PERMISSIONS
import com.asurspace.criminalintent.util.UtilPermissions.PERMISSION_ALL
import com.asurspace.criminalintent.util.UtilPermissions.hasPermissions
import com.asurspace.criminalintent.util.dateFormat
import com.asurspace.criminalintent.util.ui.PreviewFragment
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst.KEY_SELECTED_MEDIA
import droidninja.filepicker.FilePickerConst.REQUEST_CODE_PHOTO
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
class CrimeFragment : Fragment(R.layout.crime_fragment) {

    private val sharedViewModel by activityViewModels<SharedVM>()

    private val viewModel by viewModels<CrimeVM>()

    private var docPaths = ArrayList<Any>()
    private var photoPaths = ArrayList<Any>()

    private var _binding: CrimeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setCrimeId(sharedViewModel.crimeId.value)
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

        lifecycle.addObserver(viewModel)

        restoreValue()
        listenerInitialization()
        subscribeOnLD()
    }

    private fun restoreValue() {
        binding

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

            if (!hasPermissions(requireContext(), *PERMISSIONS)) {
                ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_ALL)
            } else {
                openFilePicker()
            }

        }

        binding.removeTb.setOnClickListener {
            viewModel.remove()
            with(activity as MainActivity) {
                showSnackBar(resources.getString(R.string.msg_crime_removed))
                onBackPressed()
            }
        }

        binding.crimeIv.setOnClickListener {
            (activity as MainActivity).openFragment(PreviewFragment())
            setFragmentResult(PREVIEW,
                bundleOf(IMAGE to (viewModel.imageUriLD.value ?: ""))
            )
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

    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                              // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to FragmentNameList.CRIME_FRAGMENT)
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

    private fun openFilePicker() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setActivityTheme(R.style.Theme_CriminalIntent)
            .pickPhoto(this)
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

}