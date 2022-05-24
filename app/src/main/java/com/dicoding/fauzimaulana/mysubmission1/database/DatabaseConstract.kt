package com.dicoding.fauzimaulana.mysubmission1.database

import android.net.Uri
import android.provider.BaseColumns

object DatabaseConstract {

    const val AUTHORITY = "com.dicoding.fauzimaulana.mysubmission1"
    const val SCHEME = "content"

    class UserColumns: BaseColumns {
        companion object {
            const val TABLE_NAME = "user"
            const val _ID = "_id"
            const val NAME = "name"
            const val FOLLOWERS = "followers"
            const val FOLLOWING = "following"
            const val AVATAR = "avatar"
            const val USERNAME = "username"
            const val COMPANY = "company"
            const val LOCATION = "location"
            const val REPOSITORY = "repository"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}