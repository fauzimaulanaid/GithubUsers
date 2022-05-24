package com.dicoding.fauzimaulana.mysubmission1.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.dicoding.fauzimaulana.mysubmission1.database.DatabaseConstract.AUTHORITY
import com.dicoding.fauzimaulana.mysubmission1.database.DatabaseConstract.UserColumns.Companion.CONTENT_URI
import com.dicoding.fauzimaulana.mysubmission1.database.DatabaseConstract.UserColumns.Companion.TABLE_NAME
import com.dicoding.fauzimaulana.mysubmission1.database.UserHelper

class UserProvider : ContentProvider() {

    companion object {
        private const val USER = 1
        private const val USER_USERNAME = 2
        private val sUriMather = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var userHelper: UserHelper

        init {
            //content://com.dicoding.fauzimaulana.mysubmission1/user
            sUriMather.addURI(AUTHORITY, TABLE_NAME, USER)

            //content://com.dicoding.fauzimaulana.mysubmission1/user/username
            sUriMather.addURI(AUTHORITY, "$TABLE_NAME/*", USER_USERNAME)
        }
    }

    override fun onCreate(): Boolean {
        userHelper = UserHelper.getInstance(context as Context)
        userHelper.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (sUriMather.match(uri)) {
            USER -> userHelper.queryAll()
            USER_USERNAME -> userHelper.queryByUsername(uri.lastPathSegment.toString())
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val added: Long = when (USER) {
            sUriMather.match(uri) -> userHelper.insert(contentValues)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when (USER_USERNAME) {
            sUriMather.match(uri) -> userHelper.deleteByUsername(uri.lastPathSegment.toString())
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }
}