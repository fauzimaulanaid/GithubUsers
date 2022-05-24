package com.dicoding.fauzimaulana.consumerapp.helper

import android.database.Cursor
import com.dicoding.fauzimaulana.consumerapp.User
import com.dicoding.fauzimaulana.consumerapp.database.DatabaseConstract.UserColumns

object MappingHelper {
    fun mapCursorToArrayList(userCursor: Cursor?): ArrayList<User> {
        val userList = ArrayList<User>()

        userCursor?.apply {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(UserColumns.NAME))
                val followers = getString(getColumnIndexOrThrow(UserColumns.FOLLOWERS))
                val following = getString(getColumnIndexOrThrow(UserColumns.FOLLOWING))
                val avatar = getString(getColumnIndexOrThrow(UserColumns.AVATAR))
                val username = getString(getColumnIndexOrThrow(UserColumns.USERNAME))
                val company = getString(getColumnIndexOrThrow(UserColumns.COMPANY))
                val location = getString(getColumnIndexOrThrow(UserColumns.LOCATION))
                val repository = getString(getColumnIndexOrThrow(UserColumns.REPOSITORY))
                userList.add(User(name, followers, following, avatar, username, company, location, repository))
            }
        }
        return userList
    }
    fun mapCursorToArrayListToGetAvatar(cursor: Cursor?): ArrayList<String>  {
        val avatarList = ArrayList<String>()
        cursor?.apply {
            while (moveToNext()) {
                val avatar = getString(getColumnIndexOrThrow(UserColumns.AVATAR))
                avatarList.add(avatar)
            }
        }
        return avatarList
    }
}