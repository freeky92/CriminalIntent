package com.asurspace.criminalintent.foundation

import androidx.fragment.app.Fragment

fun Fragment.navigator() = requireActivity() as Navigator

interface Navigator {
}