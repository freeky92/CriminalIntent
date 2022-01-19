package com.asurspace.criminalintent.model.sqlite

object AppSQLiteContract {

    object CrimesTable {
        const val TABLE_NAME = "crimes"

        const val COLUMN_ID = "id"
        const val COLUMN_SOLVED = "solved"
        const val COLUMN_TITLE = "title"
        const val COLUMN_SUSPECT = "suspect"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_CREATION_DATE = "creation_date"
        const val COLUMN_IMAGE_URI = "image_uri"
    }

}