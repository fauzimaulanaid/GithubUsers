package com.dicoding.fauzimaulana.mysubmission1

import android.content.Context
import android.net.ConnectivityManager

class CheckNetworkConnection() {
    fun networkCheck(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}