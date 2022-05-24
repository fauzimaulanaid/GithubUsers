package com.dicoding.fauzimaulana.consumerapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var name: String,
    var followers: String,
    var following: String,
    var avatar: String,
    var username: String,
    var company: String,
    var location: String,
    var repository: String
) : Parcelable
