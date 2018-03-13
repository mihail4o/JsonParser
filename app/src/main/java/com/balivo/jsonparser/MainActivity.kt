package com.balivo.jsonparser

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutionException
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = "MainActivity"
        private val GET_REQUEST_METHOD = "GET"
        private val READ_TIMEOUT = 15000
        private val CONNECTION_TIMEOUT = 15000
    }
    // URL for downloading json data
    private val url = "https://api.myjson.com/bins/j92d3"
    private lateinit var usersList: ArrayList<HashMap<String, String>>
    private lateinit var adapter: UsersAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Create an empty List and pass it to adapter
        usersList = ArrayList(0)
        adapter = UsersAdapter(usersList)

// Set-up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        try {
// Calling execute() method will in-turn
// execute doInBackGround() method
            val getUsers = GetUsers()
            getUsers.execute(url).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }
    /**
     * This class will download the Json data in a background
     * thread.
     */
    private inner class GetUsers : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
// show Progressbar while downloading
            progressBar.visibility = View.VISIBLE
        }
        override fun doInBackground(vararg strings: String): String? {
            var result: String?
            var inputLine: String
            val stringUrl = strings[0]
            try {
                val url = URL(stringUrl)
                val connection = url.openConnection() as HttpsURLConnection
// Set request method and timeouts
                connection.requestMethod = GET_REQUEST_METHOD
                connection.readTimeout = READ_TIMEOUT
                connection.connectTimeout = CONNECTION_TIMEOUT
                connection.connect()
                val streamReader = InputStreamReader(connection.inputStream)
                val reader = BufferedReader(streamReader)
                val stringBuilder = StringBuilder()
// In Kotlin variable assignments in conditional expressions like while,
// for loops are not allowed. So use lineSequence method of BufferedReader.
                reader.lineSequence().forEach {
                    inputLine = it
                    stringBuilder.append(inputLine)
                }
                reader.close()
                streamReader.close()
// Set the result
                result = stringBuilder.toString()
            } catch (e: MalformedURLException) {
                Log.e(TAG, "MalformedURLException: " + e.message)
                result = null
            } catch (e: IOException) {
                Log.e(TAG, "IOException: " + e.message)
                result = null
            }
            return result
        }
        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
// hide the progress bar
            progressBar.visibility = View.GONE
// update the UsersAdapter with new data
            adapter.updateAdapter(parseJson(s))
        }
    }
    /**
     * This method takes a String and converts it into Json data
     * @param result
     * @return usersList
     */
    private fun parseJson(result: String?): ArrayList<HashMap<String, String>> {
        if (result != null) {
            try {
// Here we are getting the root object, which is the
// curly braces in the sample
                val jsonObject = JSONObject(result)
// Get The JSON array "Users"
                val users = jsonObject.getJSONArray("Users")
// loop through the Array to obtain all users
                for (i in 0 until users.length()) {
// single user object
                    val obj = users.getJSONObject(i)
// email is an object
                    val emailObj = obj.getJSONObject("email")
                    val user = HashMap<String, String>()
                    user.put("id", obj.getString("id"))
                    user.put("name", obj.getString("name"))
                    user.put("age", obj.getString("age"))
                    user.put("gender", obj.getString("gender"))
                    user.put("primary_email", emailObj.getString("primary"))
                    user.put("secondary_email", emailObj.getString("secondary"))
// add user to usersList
                    usersList.add(user)
                }
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing Json: " + e.message)
            }
        } else {
            Log.d(TAG, "Error parsing Json: Empty Json")
        }
        return usersList
    }
}