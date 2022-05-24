package com.dicoding.fauzimaulana.consumerapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.dicoding.fauzimaulana.consumerapp.database.DatabaseConstract.UserColumns.Companion.CONTENT_URI
import com.dicoding.fauzimaulana.consumerapp.helper.MappingHelper
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private val mAvatarList = ArrayList<String>()
    private val mWidgetItem = ArrayList<Bitmap>()

    override fun onCreate() {
        val cursor = mContext.contentResolver.query(CONTENT_URI, null, null, null, null)
        val user = MappingHelper.mapCursorToArrayListToGetAvatar(cursor)
        mAvatarList.addAll(user)
    }

    override fun onDataSetChanged() {
        for (a in mAvatarList) {
            val url = URL(a)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            mWidgetItem.add(BitmapFactory.decodeStream(input))
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItem.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.image_view, mWidgetItem[position])

        val extras = bundleOf(
            UserFavoriteWidget.EXTRA_ITEM to position
        )
        val fillIntent = Intent()
        fillIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.image_view, fillIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}