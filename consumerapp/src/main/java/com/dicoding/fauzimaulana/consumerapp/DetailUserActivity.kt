package com.dicoding.fauzimaulana.consumerapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.contentValuesOf
import com.bumptech.glide.Glide
import com.dicoding.fauzimaulana.consumerapp.database.DatabaseConstract
import com.dicoding.fauzimaulana.consumerapp.database.DatabaseConstract.UserColumns.Companion.CONTENT_URI
import com.dicoding.fauzimaulana.consumerapp.databinding.ActivityDetailUserBinding
import com.dicoding.fauzimaulana.consumerapp.helper.MappingHelper

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var uriWithUsername: Uri

    companion object {
        const val USER_DATA = "user_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = intent.getParcelableExtra<User>(USER_DATA)

        supportActionBar?.title = getString(R.string.detail)

        binding.tvDetailName.text = user?.name

        val followers = resources.getString(R.string.users_followers)
        val following = resources.getString(R.string.users_following)
        val repo = resources.getString(R.string.users_repository)

        "$followers\n${user?.followers}".also { binding.tvDetailFollowers.text = it }
        "$following\n${user?.following}".also { binding.tvDetailFollowing.text = it }
        Glide.with(this)
            .load(user?.avatar)
            .into(binding.imgDetailUser)
        binding.tvDetailUsername.text = user?.username
        binding.tvDetailCompany.text = user?.company
        binding.tvDetailLocation.text = user?.location
        "${user?.repository} $repo".also { binding.tvDetailRepository.text = it }

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter.username = user?.username
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        supportActionBar?.elevation = 0f

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        uriWithUsername = Uri.parse(CONTENT_URI.toString() + "/" + user?.username)

        val userCursor = contentResolver.query(uriWithUsername, null, null, null, null)
        val userArray = MappingHelper.mapCursorToArrayList(userCursor)

        var statusFavorite: Boolean = userArray.size > 0
        setStatusFavorite(statusFavorite)


        binding.fabFavorite.setOnClickListener {
            if (statusFavorite) {
                showAlertDeleted(user?.name)
            } else {
                val values = contentValuesOf(
                    DatabaseConstract.UserColumns.NAME to user?.name,
                    DatabaseConstract.UserColumns.FOLLOWERS to user?.followers,
                    DatabaseConstract.UserColumns.FOLLOWING to user?.following,
                    DatabaseConstract.UserColumns.AVATAR to user?.avatar,
                    DatabaseConstract.UserColumns.USERNAME to user?.username,
                    DatabaseConstract.UserColumns.COMPANY to user?.company,
                    DatabaseConstract.UserColumns.LOCATION to user?.location,
                    DatabaseConstract.UserColumns.REPOSITORY to user?.repository
                )

                val result = contentResolver.insert(CONTENT_URI, values)

                if (result != null) {
                    val added = resources.getString(R.string.added, user?.name)
                    Toast.makeText(this, added, Toast.LENGTH_SHORT).show()
                    statusFavorite = true
                    setStatusFavorite(statusFavorite)
                } else {
                    val addedFailed = resources.getString(R.string.added_failed, user?.name)
                    Toast.makeText(this, addedFailed, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun showAlertDeleted(name: String?) {
        val dialogTitle = resources.getString(R.string.warning)
        val dialogMessage = resources.getString(R.string.ask, name)

        val textYes = resources.getString(R.string.yes)
        val textNo = resources.getString(R.string.no)

        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton(textYes) { _,_ ->
                val result = contentResolver.delete(uriWithUsername, null, null)
                if (result > 0) {
                    val removed = resources.getString(R.string.removed, name)
                    Toast.makeText(this, removed, Toast.LENGTH_SHORT).show()
                    setStatusFavorite(false)
                } else {
                    val failedToRemoved = resources.getString(R.string.removed_failed, name)
                    Toast.makeText(this, failedToRemoved, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(textNo) { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        if (statusFavorite) {
            binding.fabFavorite.setImageResource(R.drawable.baseline_favorite_white_48)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.baseline_favorite_border_white_48)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_only_setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting) {
            val mIntent = Intent(this@DetailUserActivity, SettingsActivity::class.java)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}