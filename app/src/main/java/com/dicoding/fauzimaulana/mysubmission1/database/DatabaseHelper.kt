package com.dicoding.fauzimaulana.mysubmission1.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dicoding.fauzimaulana.mysubmission1.database.DatabaseConstract.UserColumns.Companion.TABLE_NAME
import com.dicoding.fauzimaulana.mysubmission1.database.DatabaseConstract.UserColumns

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "dbuserfavorite"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_NAME = "CREATE TABLE $TABLE_NAME" +
                "(${UserColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${UserColumns.NAME} TEXT NOT NULL," +
                "${UserColumns.FOLLOWERS} TEXT NOT NULL," +
                "${UserColumns.FOLLOWING} TEXT NOT NULL," +
                "${UserColumns.AVATAR} TEXT NOT NULL," +
                "${UserColumns.USERNAME} TEXT NOT NULL," +
                "${UserColumns.COMPANY} TEXT NOT NULL," +
                "${UserColumns.LOCATION} TEXT NOT NULL," +
                "${UserColumns.REPOSITORY} TEXT NOT NULL)"
    }

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(SQL_CREATE_TABLE_NAME)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(database)
    }
}