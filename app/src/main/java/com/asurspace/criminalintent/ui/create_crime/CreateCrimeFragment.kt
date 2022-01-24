package com.asurspace.criminalintent.ui.create_crime

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
import androidx.fragment.app.viewModels
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.databinding.CreateCrimeFragmentBinding
import com.asurspace.criminalintent.util.FragmentNameList
import com.asurspace.criminalintent.util.UtilPermissions
import com.asurspace.criminalintent.util.dateFormat
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
class CreateCrimeFragment : Fragment(R.layout.create_crime_fragment) {

    private val viewModel by viewModels<CreateCrimeVM>()

    private var docPaths = ArrayList<Any>()
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

            if (!UtilPermissions.hasPermissions(requireContext(), *UtilPermissions.PERMISSIONS)) {
                ActivityCompat.requestPermissions(requireActivity(),
                    UtilPermissions.PERMISSIONS,
                    UtilPermissions.PERMISSION_ALL
                )
            } else {
                openFilePicker()
            }

        }

        binding.createTb.setOnClickListener {
            viewModel.addCrime()
            with(activity as MainActivity) {
                showSnackBar(resources.getString(R.string.msg_crime_created))
                onBackPressed()
            }
        }

    }

    private fun subscribeOnLD() {
        viewModel.imageUriLD.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.crimeIv.setImageURI(Uri.parse(it))
            }
        }

    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,                              // !!CHANGE FragmentNameList.CRIME_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to FragmentNameList.CREATE_CRIME_FRAGMENT)
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
        // Сюда копируем соответствующий код с сайта
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setActivityTheme(R.style.Theme_CriminalIntent)
            .pickPhoto(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FilePickerConst.REQUEST_CODE_PHOTO -> if (resultCode == Activity.RESULT_OK && data != null) {

                data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
                    ?.let { photoPaths.addAll(it) }
            }
            FilePickerConst.REQUEST_CODE_DOC -> if (resultCode == Activity.RESULT_OK && data != null) {
                data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_DOCS)
                    ?.let { docPaths.addAll(it) }
            }
        }
        if (photoPaths.isNotEmpty()) {
            viewModel.setUpdatedImage((photoPaths.first() as Uri).toString())
            photoPaths.clear()
        }
    }

}
