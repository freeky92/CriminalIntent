package com.asurspace.criminalintent.presentation.ui.state

sealed class UIState<out T>(val date: T? = null) {
    object Empty : UIState<Nothing>()
    object Pending : UIState<Nothing>()
    class Success<T>(val data: T) : UIState<T>(data)
    class Error(val error: ErrorModel) : UIState<Nothing>()
}