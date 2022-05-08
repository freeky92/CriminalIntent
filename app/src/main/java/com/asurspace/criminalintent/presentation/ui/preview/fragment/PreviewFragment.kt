package com.asurspace.criminalintent.presentation.ui.preview.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.asurspace.criminalintent.common.utils.PREVIEW_FRAGMENT
import com.asurspace.criminalintent.databinding.FragmentPreviewBinding
import com.asurspace.criminalintent.presentation.ui.MainActivity

class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setImage(requireArguments().getString(ARG_URI) ?: "")

        binding.previewIv.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fragmentResumeResult() {
        requireActivity().supportFragmentManager.setFragmentResult(
            MainActivity.NAVIGATION_EVENT,              // !!CHANGE FragmentNameList.PREVIEW_FRAGMENT VALUE ON COPY!!
            bundleOf(MainActivity.NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY to PREVIEW_FRAGMENT)
        )
    }

    override fun onResume() {
        super.onResume()
        fragmentResumeResult()
    }

    private fun setImage(uri: String) {
        if (uri.isNotBlank()) {
            binding.previewIv.setImageURI(Uri.parse(uri))
        }
    }

    companion object {
        @JvmStatic
        private val ARG_URI = "ARG_URI"

        @JvmStatic
        private val TAG = "PreviewFragment"

        @JvmStatic
        fun newInstance(uri: String) = PreviewFragment().apply {
            Log.d(TAG, uri)
            arguments = bundleOf(
                ARG_URI to uri
            )
        }
    }

}