package com.dicoding.fauzimaulana.mysubmission1.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.dicoding.fauzimaulana.mysubmission1.StackRemoteViewsFactory

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory = StackRemoteViewsFactory(this.applicationContext)
}