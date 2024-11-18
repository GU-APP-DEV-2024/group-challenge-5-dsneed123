package com.zybooks.workingwithdata

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.media.MediaPlayer
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
    private var startDate: String = ""
    private var endDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fireballs)

        recyclerView = findViewById(R.id.recyclerView)
        textView = findViewById(R.id.textView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        cmeAdapter = CMEAdapter(cmeList)
        recyclerView.adapter = cmeAdapter
        val startDateButton: Button = findViewById(R.id.startDateButton)
        val endDateButton: Button = findViewById(R.id.endDateButton)

        startDateButton.setOnClickListener {
            showDatePicker(true) // true indicates start date
        }

        endDateButton.setOnClickListener {
            showDatePicker(false) // false indicates end date
        }
    }

    private fun playBoom(times: Int) {
        if (times > 0) {
            mediaPlayer = MediaPlayer.create(this, R.raw.boom)
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
                playBoom(times - 1)
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

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "${selectedYear}-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}"
            if (isStartDate) {
                startDate = selectedDate
            } else {
                endDate = selectedDate
            }

            val dateButton = if (isStartDate) findViewById<Button>(R.id.startDateButton) else findViewById<Button>(R.id.endDateButton)
            dateButton.text = "Selected Date: $selectedDate"

            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                getCMEAnalysis(this, startDate = startDate, endDate = endDate, speed = 500)
            }
        }, year, month, day)

        datePickerDialog.show()
    }

    fun getCMEAnalysis(context: Context,
                       startDate: String,
                       endDate: String,
                       mostAccurateOnly: Boolean = true,
                       completeEntryOnly: Boolean = true,
                       speed: Int = 0,
                       halfAngle: Int = 0,
                       catalog: String = "ALL",
                       keyword: String = "NONE") {

        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }

        val baseUrl = "https://api.nasa.gov/DONKI/CMEAnalysis"
        val apiKey = BuildConfig.NASA_API_KEY
        val url = "$baseUrl?startDate=$startDate&endDate=$endDate&mostAccurateOnly=$mostAccurateOnly" +
                "&completeEntryOnly=$completeEntryOnly&speed=$speed&halfAngle=$halfAngle&catalog=$catalog" +
                "&keyword=$keyword&api_key=$apiKey"

        val requestQueue: RequestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    cmeList.clear()

                    for (i in 0 until response.length()) {
                        val cmeObject = response.getJSONObject(i)
                        val time = cmeObject.getString("time21_5")
                        val latitude = cmeObject.getDouble("latitude")
                        val longitude = cmeObject.getDouble("longitude")
                        val halfAngle = cmeObject.getInt("halfAngle")
                        val speed = cmeObject.getInt("speed")
                        val associatedCMEID = cmeObject.getString("associatedCMEID")
                        val note = cmeObject.getString("note")
                        val catalog = cmeObject.getString("catalog")
                        val link = cmeObject.getString("link")

                        val cme = CME(time, latitude, longitude, halfAngle, speed, associatedCMEID, note, catalog, link)
                        cmeList.add(cme)
                    }

                    cmeAdapter.notifyDataSetChanged()

                    textView.text = "There were ${cmeList.size} Big Booms (Coronal Mass ejections) from $startDate to $endDate"
                    playBoom(cmeList.size)
                } catch (e: Exception) {
                    println("Error parsing response: ${e.message}")
                }
            },
            { error ->
                println("Error occurred: ${error.message}")
            }
        )

        requestQueue.add(jsonArrayRequest)
    }
}
