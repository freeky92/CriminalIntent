package com.asurspace.criminalintent.foundation

import androidx.annotation.StringRes

interface ProviderCustomTitle {

    @StringRes
    fun getTitle(): Int

}