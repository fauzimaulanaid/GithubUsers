package com.dicoding.fauzimaulana.mysubmission1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.fauzimaulana.mysubmission1.databinding.FragmentFollowingBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class FollowingFragment : Fragment() {

    companion object {
        private const val STATE_LIST = "state_list"
        private val ARG_USERNAME = "username"

        fun newInstance(username: String): FollowingFragment {
            val fragment = FollowingFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var followingFollowingAdapter: FollowersFollowingAdapter
    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!
    private val list = ArrayList<User>()
    private val token = BuildConfig.GITHUB_TOKEN

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        followingFollowingAdapter = FollowersFollowingAdapter(list)
        showRecycleList()

        val isConnected: Boolean = CheckNetworkConnection().networkCheck(requireContext())
        if (savedInstanceState == null) {
            if (isConnected) {
                val username = arguments?.getString(ARG_USERNAME)
                getAllFollowing(username.toString())
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

    private fun getAllFollowing(username: String) {
        binding.progressBarFollowing.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$username/following"
        client.addHeader("Authorization", token)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                //if the connection is successful
                binding.progressBarFollowing.visibility = View.INVISIBLE

                val result = String(responseBody)

                val tagGetAllFollowing = "getAllFollowing()"
                Log.d(tagGetAllFollowing, result)

                try {
                    val responseArray = JSONArray(result)

                    for (i in 0 until responseArray.length()){
                        val item = responseArray.getJSONObject(i)
                        val followingUsername = item.getString("login")
                        getFollowing(followingUsername)
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
                binding.progressBarFollowing.visibility = View.INVISIBLE

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

    private fun getFollowing(selectedUsername: String) {
        binding.progressBarFollowing.visibility = View.VISIBLE

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
                binding.progressBarFollowing.visibility = View.INVISIBLE

                val result = String(responseBody)

                val tagGetFollowing = "getFollowing()"
                Log.d(tagGetFollowing, result)

                try {
                    val responseObject = JSONObject(result)

                    val name = responseObject.getString("name")
                    val followers = responseObject.getString("followers")
                    val following = responseObject.getString("following")
                    val avatar = responseObject.getString("avatar_url")
                    val username = responseObject.getString("login")
                    val company = responseObject.getString("company")
                    val location = responseObject.getString("location")
                    val repo = responseObject.getString("public_repos")

                    list.add(User(name, followers, following, avatar, username, company, location, repo))
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
                binding.progressBarFollowing.visibility = View.INVISIBLE

                val errorMsg = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun showRecycleList() {
        binding.rvFollowing.layoutManager = LinearLayoutManager(activity)
        binding.rvFollowing.adapter = followingFollowingAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}