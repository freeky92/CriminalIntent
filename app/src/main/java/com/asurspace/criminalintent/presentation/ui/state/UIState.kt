package com.asurspace.criminalintent.presentation.ui.state

sealed class UIState<out T>(val date: T? = null) {
    object Empty : UIState<Nothing>()
    object Pending : UIState<Nothing>()
    class Success<T>(val data: T) : UIState<T>(data)
    class Error(val error: ErrorModel) : UIState<Nothing>()
}

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(data: T? = null, message: String) : Resource<T>(data, message)
    class Loading<T>(data: T?) : Resource<T>(data)
}