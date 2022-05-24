package com.dicoding.fauzimaulana.consumerapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.fauzimaulana.consumerapp.databinding.FragmentFollowersBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class FollowersFragment : Fragment() {

    companion object {
        private const val STATE_LIST = "state_list"
        private val ARG_USERNAME = "username"

        fun newInstance(username: String): FollowersFragment {
            val fragment = FollowersFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var followersFollowingAdapter: FollowersFollowingAdapter
    private var _binding: FragmentFollowersBinding? = null
    private val binding get() = _binding!!
    private val list = ArrayList<User>()
    private val token = BuildConfig.GITHUB_TOKEN

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowersBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        followersFollowingAdapter = FollowersFollowingAdapter(list)
        showRecycleList()

        val isConnected: Boolean = CheckNetworkConnection().networkCheck(requireContext())
        if (savedInstanceState == null) {
            if (isConnected) {
                val username = arguments?.getString(ARG_USERNAME)
                getAllFollowers(username.toString())
            } else {
                showAlertNoInternet()
            }
        } else {
            val stateList = savedInstanceState.getParcelableArrayList<User>(STATE_LIST)
            if (stateList != null) {
                list.addAll(stateList)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(STATE_LIST, list)
    }

    private fun showAlertNoInternet() {
        val dialogTitle = resources.getString(R.string.no_internet_title)
        val dialogMessage = resources.getString(R.string.no_internet_message_followers_following)

        val alertDialogBuilder = AlertDialog.Builder(requireActivity())

        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setNegativeButton("Ok") { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun getAllFollowers(username: String) {
        binding.progressBarFollowers.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$username/followers"
        client.addHeader("Authorization", token)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                //if the connection is successful
                binding.progressBarFollowers.visibility = View.INVISIBLE

                val result = String(responseBody)

                val tagGetAllFollowers = "getAllFollowers()"
                Log.d(tagGetAllFollowers, result)

                try {
                    val responseArray = JSONArray(result)

                    for (i in 0 until responseArray.length()) {
                        val item = responseArray.getJSONObject(i)
                        val followerUsername = item.getString("login")
                        getFollowers(followerUsername)
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                //if the connection is failure
                binding.progressBarFollowers.visibility = View.INVISIBLE

                val errorMsg = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getFollowers(selectedUsername: String) {
        binding.progressBarFollowers.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$selectedUsername"
        client.addHeader("Authorization", token)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                //if the connection is successful
                binding.progressBarFollowers.visibility = View.INVISIBLE

                val result = String(responseBody)

                val tagGetFollowers = "getFollowers()"
                Log.d(tagGetFollowers, result)

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
                    showRecycleList()
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                //if the connection is failure
                binding.progressBarFollowers.visibility = View.INVISIBLE

                val errorMsg = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showRecycleList() {
        binding.rvFollowers.layoutManager = LinearLayoutManager(activity)
        binding.rvFollowers.adapter = followersFollowingAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}