package com.dicoding.fauzimaulana.mysubmission1

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.fauzimaulana.mysubmission1.database.DatabaseConstract.UserColumns.Companion.CONTENT_URI
import com.dicoding.fauzimaulana.mysubmission1.databinding.ActivityUserFavoriteBinding
import com.dicoding.fauzimaulana.mysubmission1.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UserFavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserFavoriteBinding
    private lateinit var adapter: FavoriteUserAdapter
    private val list = ArrayList<User>()

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = resources.getString(R.string.title_favorite)
        supportActionBar?.title = title

        adapter = FavoriteUserAdapter(list)
        binding.rvUsers.setHasFixedSize(true)
        binding.rvUsers.addItemDecoration(DividerItemDecoration(this@UserFavoriteActivity,
            LinearLayoutManager.VERTICAL))
        showRecyclerList()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                list.clear()
                loadFavoriteUserAsync()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        if (savedInstanceState == null) {
            loadFavoriteUserAsync()
        } else {
            val stateList = savedInstanceState.getParcelableArrayList<User>(EXTRA_STATE)
            if (stateList != null) {
                list.addAll(stateList)
            }
        }
        setListClickAction()
    }

    private fun loadFavoriteUserAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE

            val differedUser = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val user = differedUser.await()
            binding.progressBar.visibility = View.INVISIBLE
            if (user.size > 0) {
                list.addAll(user)
                showRecyclerList()
            } else {
                showRecyclerList()
                val empty = resources.getString(R.string.no_fav_user)
                Toast.makeText(this@UserFavoriteActivity, empty, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRecyclerList() {
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, list)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setListClickAction() {
        adapter.setOnItemClickCallbackFavorite(object  : FavoriteUserAdapter.OnItemClickCallbackFavorite {
            override fun onItemClick(data: User) {
                val choose = resources.getString(R.string.choose, data.name)
                Toast.makeText(this@UserFavoriteActivity, choose, Toast.LENGTH_SHORT).show()
                val moveIntent = Intent(this@UserFavoriteActivity, DetailUserActivity::class.java)
                moveIntent.putExtra(DetailUserActivity.USER_DATA, data)
                startActivity(moveIntent)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_only_setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> {
                val mIntent = Intent(this@UserFavoriteActivity, SettingsActivity::class.java)
                startActivity(mIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}