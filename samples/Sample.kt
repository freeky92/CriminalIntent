


fun openFilePicker() {
    FilePickerBuilder.instance
        .setMaxCount(1)
        .setActivityTheme(R.style.Theme_CriminalIntent)
        .pickPhoto(this)
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
        REQUEST_CODE_PHOTO -> if (resultCode == Activity.RESULT_OK && data != null) {
            data.getParcelableArrayListExtra<Uri>(KEY_SELECTED_MEDIA)
                ?.let { photoPaths.addAll(it) }
        }
    }
    if (photoPaths.isNotEmpty()) {
        viewModel.setUpdatedImage((photoPaths.first() as Uri).toString())
        photoPaths.clear()
    }
}
