package com.dicoding.fauzimaulana.consumerapp

import android.content.Context
import android.net.ConnectivityManager

class CheckNetworkConnection() {
    fun networkCheck(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}