package com.asurspace.criminalintent.navigation

import androidx.annotation.StringRes

interface ProviderCustomTitle {

    @StringRes
    fun getTitle(): Int

}