package com.asurspace.criminalintent.presentation.ui.crime.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.common.utils.UtilPermissions.PERMISSIONS
import com.asurspace.criminalintent.common.utils.UtilPermissions.hasPermissions
import com.asurspace.criminalintent.common.utils.dateFormat
import com.asurspace.criminalintent.databinding.CrimeFragmentBinding
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.model.crimes.entities.CrimeAdditional.emptyCrime
import com.asurspace.criminalintent.navigation.ProviderCustomTitle
import com.asurspace.criminalintent.presentation.ui.MainActivity
import com.asurspace.criminalintent.presentation.ui.crime.viewmodel.EditCrimeVM
import com.asurspace.criminalintent.presentation.ui.preview.fragment.PreviewFragment
import com.asurspace.criminalintent.presentation.ui.state.UIState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EditCrimeFragment : Fragment(R.layout.crime_fragment), ProviderCustomTitle {

    private lateinit var snackBar: Snackbar

    private val permissionLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionResult
    )
    private val pickImageLauncher = registerForActivityResult(OpenDocument(), ::onGotImageResult)

    private var _binding: CrimeFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<EditCrimeVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.setCrime(requireArguments().getParcelable(ARG_CRIME) ?: emptyCrime())
        }
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

        setEditChangeListeners()
        listenerInitialization()
        subscribeOnLD()
    }

    private fun setEditChangeListeners() {
        binding.crimeTitleInput.editText?.addTextChangedListener {
            viewModel.setUpdatedTitle(it.toString())
        }
        binding.crimeSuspectInput.editText?.addTextChangedListener {
            viewModel.setUpdatedSuspect(it.toString())
        }
        binding.crimeDescriptionInput.editText?.addTextChangedListener {
            viewModel.setUpdatedDescription(it.toString())
        }
    }

    private fun listenerInitialization() {
        binding.apply {

            snackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
            removeTb.setOnClickListener {
                snackBar.setText("Are you sure?")
                    .setBackgroundTint(Color.WHITE)
                    .setTextColor(Color.BLACK)

                val sBarPar = snackBar.view.findViewById<AppCompatTextView>(R.id.snackbar_text)
                sBarPar?.let {
                    it.textSize = 16f
                    it.textAlignment = View.TEXT_ALIGNMENT_CENTER
                }

                root.setOnClickListener { snackBar.dismiss() }

                snackBar.view.setOnClickListener {
                    snackBar.dismiss()
                }

                snackBar.setAction("Delete") {
                    viewModel.remove()
                }.show()
            }

            checkboxSolved.setOnCheckedChangeListener { _, b ->
                viewModel.setSolvedState(b)
            }

            setImageButton.setOnClickListener {
                permissionLauncher.launch(PERMISSIONS)
            }

            crimeIv.setOnClickListener {
                val uri = viewModel.uiState.value.date?.imageURI
                if (!uri.isNullOrEmpty()) {
                    Log.d(TAG, uri.toString())
                    (activity as MainActivity).openFragment(PreviewFragment.newInstance(uri))
                }
            }
            crimeIv.setOnLongClickListener {
                crimeIv.setImageURI(null)
                viewModel.setUpdatedImage(Uri.EMPTY)
                return@setOnLongClickListener true
            }

        }
    }

    private fun subscribeOnLD() {
        viewModel.uiState.asLiveData().observe(viewLifecycleOwner) { state ->
            hideAll()
            when (state) {
                is UIState.Empty -> {
                    binding.noData.visibility = View.VISIBLE
                }
                is UIState.Pending -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is UIState.Error -> {
                    binding.empty.text = getString(R.string.error)
                    binding.noData.visibility = View.VISIBLE
                }
                is UIState.Success -> {
                    binding.content.visibility = View.VISIBLE
                    val data = state.data
                    binding.checkboxSolved.isChecked = data.solved ?: false
                    binding.crimeTitleInput.editText?.setText(data.title.toString())
                    binding.crimeSuspectInput.editText?.setText(data.suspect.toString())
                    binding.crimeDescriptionInput.editText?.setText(data.description.toString())
                    binding.dateTv.text =
                        getString(
                            R.string.creation_date_plus,
                            dateFormat.format(Date(data.creationDate ?: 0))
                        )
                    val imageUri = data.imageURI
                    if (!imageUri.isNullOrBlank() /*&& imageUri != "null"*/) {
                        binding.crimeIv.setImageURI(Uri.parse(imageUri))
                    }
                }
            }
        }

        viewModel.isRemoved.observe(viewLifecycleOwner) {
            with(activity as MainActivity) {
                showSnackBar(resources.getString(R.string.msg_crime_removed))
                onBackPressed()
            }
        }

    }

    private fun hideAll() {
        binding.noData.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.content.visibility = View.GONE
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

    override fun onPause() {
        super.onPause()
        if (snackBar.isShown) {
            snackBar.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTitle() = R.string.crime_details

    companion object {
        @JvmStatic
        fun newInstance(crime: Crime) = EditCrimeFragment().apply {
            arguments = bundleOf(ARG_CRIME to crime)
        }

        @JvmStatic
        private val TAG = "EditCrimeFragment"

        @JvmStatic
        private val ARG_CRIME = "ARG_CRIME"

    }
}