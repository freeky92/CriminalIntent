package com.asurspace.criminalintent.presentation.ui.state

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorModel(
    @DrawableRes val icon: Int = -1,
    @StringRes val title: Int,
    @StringRes val message: Int = -1,
    @StringRes val positive: Int = -1,
    @StringRes val neutral: Int = -1,
    @StringRes val negative: Int = -1
) : Parcelable