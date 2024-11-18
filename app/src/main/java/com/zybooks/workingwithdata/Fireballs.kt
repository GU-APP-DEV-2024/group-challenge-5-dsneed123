package com.zybooks.workingwithdata

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonArrayRequest
import java.text.SimpleDateFormat
import java.util.*

class Fireballs : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private var playCount = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var cmeAdapter: CMEAdapter
    private val cmeList = mutableListOf<CME>()
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fireballs)

        recyclerView = findViewById(R.id.recyclerView)
        textView = findViewById(R.id.textView)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        cmeAdapter = CMEAdapter(cmeList)
        recyclerView.adapter = cmeAdapter

        val playButton: Button = findViewById(R.id.playButton)

        playButton.setOnClickListener {
            playBoomFiveTimes()
        }

        // Fetch CME data
        getCMEAnalysis(this, startDate = "2024-10-01", endDate = "2024-10-31", speed = 500)
    }

    private fun playBoomFiveTimes() {
        playCount = 0
        playBoom()
    }

    private fun playBoom() {
        if (playCount < 5) {
            mediaPlayer = MediaPlayer.create(this, R.raw.boom)
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
                playCount++
                playBoom()
            }
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    fun getCMEAnalysis(context: Context,
                       startDate: String = getDefaultStartDate(),
                       endDate: String = getDefaultEndDate(),
                       mostAccurateOnly: Boolean = true,
                       completeEntryOnly: Boolean = true,
                       speed: Int = 0,
                       halfAngle: Int = 0,
                       catalog: String = "ALL",
                       keyword: String = "NONE") {

        // API base URL for CME Analysis
        val baseUrl = "https://api.nasa.gov/DONKI/CMEAnalysis"

        // API Key (replace with your actual API key)
        val apiKey = BuildConfig.NASA_API_KEY

        // Construct the URL with query parameters
        val url = "$baseUrl?startDate=$startDate&endDate=$endDate&mostAccurateOnly=$mostAccurateOnly" +
                "&completeEntryOnly=$completeEntryOnly&speed=$speed&halfAngle=$halfAngle&catalog=$catalog" +
                "&keyword=$keyword&api_key=$apiKey"

        // Create a RequestQueue using Volley
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)

        // Create a JsonArrayRequest to handle the API response
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // Handle the successful response (response is a JSONArray)
                try {
                    // Clear the current list of CMEs
                    cmeList.clear()

                    // Iterate through the array of CME objects
                    for (i in 0 until response.length()) {
                        val cmeObject = response.getJSONObject(i)

                        // Extract relevant fields from the CME object
                        val time = cmeObject.getString("time21_5")
                        val latitude = cmeObject.getDouble("latitude")
                        val longitude = cmeObject.getDouble("longitude")
                        val speed = cmeObject.getInt("speed")

                        // Create a CME object and add it to the list
                        val cme = CME(time, latitude, longitude, speed)
                        cmeList.add(cme)
                    }

                    // Notify the adapter that the data has changed
                    cmeAdapter.notifyDataSetChanged()

                    // Update the "big booms" message
                    textView.text = "There were ${cmeList.size} big booms for the selected date range."
                } catch (e: Exception) {
                    println("Error parsing response: ${e.message}")
                }
            },
            { error ->
                // Handle error
                println("Error occurred: ${error.message}")
            }
        )

        // Add the request to the RequestQueue
        requestQueue.add(jsonArrayRequest)
    }

    // Function to get the default start date (30 days ago)
    fun getDefaultStartDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)  // Subtract 30 days
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

    // Function to get the default end date (current date)
    fun getDefaultEndDate(): String {
        val calendar = Calendar.getInstance()
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }
}
