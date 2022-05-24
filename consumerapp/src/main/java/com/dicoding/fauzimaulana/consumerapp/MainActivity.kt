package com.dicoding.fauzimaulana.consumerapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.fauzimaulana.consumerapp.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listUserAdapterTest: ListUserAdapter
    private val list = ArrayList<User>()
    private val token = BuildConfig.GITHUB_TOKEN

    companion object {
        private const val STATE_LIST = "state_list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listUserAdapterTest = ListUserAdapter(list)
        binding.rvUsers.setHasFixedSize(true)
        binding.rvUsers.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
        showRecyclerList()
        Log.d("buildConfig", token)

        val isConnected: Boolean = CheckNetworkConnection().networkCheck(applicationContext)

        if (savedInstanceState == null) {
            if (isConnected) {
                getAllUsers()
            } else {
                showAlertNoInternet()
            }
        } else {
            val stateList = savedInstanceState.getParcelableArrayList<User>(STATE_LIST)
            if (stateList != null) {
                list.addAll(stateList)
            }
        }
        setListClickAction()
    }

    private fun showRecyclerList() {
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = listUserAdapterTest
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(STATE_LIST, list)
    }

    private fun showAlertNoInternet() {
        val dialogTitle = resources.getString(R.string.no_internet_title)
        val dialogMessage = resources.getString(R.string.no_internet_message)

        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setNegativeButton("Ok") { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun getAllUsers() {
        binding.progressBar.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://api.github.com/users"
        client.addHeader("Authorization", token)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                //if the connection is successful
                binding.progressBar.visibility = View.INVISIBLE

                val result = String(responseBody)

                val tagShowAllUsers = "getAllUsers()"
                Log.d(tagShowAllUsers, result)

                try {
                    val responseArray = JSONArray(result)

                    for (i in 0 until responseArray.length()) {
                        val item = responseArray.getJSONObject(i)
                        val username = item.getString("login")
                        getUser(username)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                //if the connection is failure
                binding.progressBar.visibility = View.INVISIBLE

                val errorMsg = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getListUsers(search: String) {
        binding.progressBar.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://api.github.com/search/users?q=$search"
        client.addHeader("Authorization", token)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                //if the connection is successful
                binding.progressBar.visibility = View.INVISIBLE

                val result = String(responseBody)

                val tagGetListUsers = "getListUsers()"
                Log.d(tagGetListUsers, result)

                try {
                    val responseObject = JSONObject(result)
                    val items = responseObject.getJSONArray("items")

                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val username = item.getString("login")
                        getUser(username)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                //if the connection is failure
                binding.progressBar.visibility = View.INVISIBLE

                val errorMsg = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUser(selectedUsername: String) {
        binding.progressBar.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$selectedUsername"
        client.addHeader("Authorization", token)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                //if the connection is successful
                binding.progressBar.visibility = View.INVISIBLE

                val result = String(responseBody)

                val tagGetUser = "getUser()"
                Log.d(tagGetUser, result)

                try {
                    val responseObject = JSONObject(result)

                    val name = responseObject.getString("name")
                    val follower = responseObject.getString("followers")
                    val following = responseObject.getString("following")
                    val avatar = responseObject.getString("avatar_url")
                    val username = responseObject.getString("login")
                    val company = responseObject.getString("company")
                    val location = responseObject.getString("location")
                    val repo = responseObject.getString("public_repos")

                    list.add(User(name, follower, following, avatar, username, company, location, repo))
                    showRecyclerList()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                //if the connection is failure
                binding.progressBar.visibility = View.INVISIBLE

                val errorMsg = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setListClickAction() {
        listUserAdapterTest.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClick(data: User) {
                val choose = resources.getString(R.string.choose, data.name)
                Toast.makeText(this@MainActivity,choose, Toast.LENGTH_SHORT).show()
                val moveIntent = Intent(this@MainActivity, DetailUserActivity::class.java)
                moveIntent.putExtra(DetailUserActivity.USER_DATA, data)
                startActivity(moveIntent)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isEmpty()) {
                    return true
                } else {
                    val isConnected: Boolean = CheckNetworkConnection().networkCheck(applicationContext)
                    if (isConnected) {
                        list.clear()
                        getListUsers(query)
                    } else {
                        showAlertNoInternet()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> {
                val mIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(mIntent)
            }
            R.id.favorite -> {
                val mIntent = Intent(this@MainActivity, UserFavoriteActivity::class.java)
                startActivity(mIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}