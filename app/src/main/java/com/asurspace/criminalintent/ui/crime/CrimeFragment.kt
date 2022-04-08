package com.asurspace.criminalintent.ui.crime

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.asurspace.criminalintent.MainActivity
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.databinding.CrimeFragmentBinding
import com.asurspace.criminalintent.foundation.ProviderCustomTitle
import com.asurspace.criminalintent.model.crimes.entities.Crime
import com.asurspace.criminalintent.ui.PreviewFragment
import com.asurspace.criminalintent.util.*
import com.asurspace.criminalintent.util.UtilPermissions.PERMISSIONS
import com.asurspace.criminalintent.util.UtilPermissions.hasPermissions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CrimeFragment : Fragment(R.layout.crime_fragment), ProviderCustomTitle {

    private lateinit var snackBar: Snackbar

    private val permissionLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionResult
    )
    private val pickImageLauncher = registerForActivityResult(OpenDocument(), ::onGotImageResult)

    private var _binding: CrimeFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<CrimeVM>()

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

        snackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT)
        binding.removeTb.setOnClickListener {
            snackBar = Snackbar.make(
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

            binding.root.setOnClickListener { snackBar.dismiss() }

            snackBar.view.setOnClickListener {
                snackBar.dismiss()
            }

            snackBar.setAction("Delete") {
                viewModel.remove()
            }.show()
        }

        with(binding.crimeIv) {
            setOnClickListener {
                val uri = viewModel.imageUriLD.value.toString()
                if (uri != "null") {
                    (activity as MainActivity).openFragment(PreviewFragment())
                    setImageResult(uri)
                }
            }
            setOnLongClickListener {
                viewModel.setUpdatedImage(Uri.EMPTY)
                true
            }

        }

    }

    private fun subscribeOnLD() {
        viewModel.crimeLD.observe(viewLifecycleOwner) {
            viewModel.setFields()
            restoreValue()
        }

        viewModel.imageUriLD.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.crimeIv.setImageURI(it)
            }
        }

        viewModel.isRemoved.observe(viewLifecycleOwner) {
            with(activity as MainActivity) {
                showSnackBar(resources.getString(R.string.msg_crime_removed))
                onBackPressed()
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
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to CRIME_FRAGMENT)
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
        if (hasPermissions(requireContext(), *results.keys.toTypedArray())) {
            pickImageLauncher.launch(arrayOf("image/*"))
        } else { //false
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
}