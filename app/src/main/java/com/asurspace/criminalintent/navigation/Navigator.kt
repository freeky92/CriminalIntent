package com.asurspace.criminalintent.navigation

import androidx.fragment.app.Fragment

fun Fragment.navigator() = requireActivity() as Navigator

interface Navigator {

    fun hideToolbar(status: Boolean)

}