package com.dicoding.fauzimaulana.consumerapp.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.dicoding.fauzimaulana.consumerapp.StackRemoteViewsFactory

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory = StackRemoteViewsFactory(this.applicationContext)
}