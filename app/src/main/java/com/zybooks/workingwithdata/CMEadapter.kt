package com.zybooks.workingwithdata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Updated CME data class with additional fields
data class CME(
    val time: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Int,
    val halfAngle: Int,
    val associatedCMEID: String,
    val note: String,
    val catalog: String,
    val link: String
)

class CMEAdapter(private val cmeList: List<CME>) : RecyclerView.Adapter<CMEAdapter.CMEViewHolder>() {

    class CMEViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val latitudeTextView: TextView = itemView.findViewById(R.id.latitudeTextView)
        val longitudeTextView: TextView = itemView.findViewById(R.id.longitudeTextView)
        val speedTextView: TextView = itemView.findViewById(R.id.speedTextView)
        val halfAngleTextView: TextView = itemView.findViewById(R.id.halfAngleTextView)
        val associatedCMEIDTextView: TextView = itemView.findViewById(R.id.associatedCMEIDTextView)
        val noteTextView: TextView = itemView.findViewById(R.id.noteTextView)
        val catalogTextView: TextView = itemView.findViewById(R.id.catalogTextView)
        val linkTextView: TextView = itemView.findViewById(R.id.linkTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CMEViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cme_item, parent, false)
        return CMEViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CMEViewHolder, position: Int) {
        val currentCME = cmeList[position]

        // Set the text for each TextView using the data from the current CME object
        holder.timeTextView.text = "Time: ${currentCME.time}"
        holder.latitudeTextView.text = "Latitude: ${currentCME.latitude}"
        holder.longitudeTextView.text = "Longitude: ${currentCME.longitude}"
        holder.speedTextView.text = "Speed: ${currentCME.speed} km/s"
        holder.halfAngleTextView.text = "Half Angle: ${currentCME.halfAngle}"
        holder.associatedCMEIDTextView.text = "Associated CME ID: ${currentCME.associatedCMEID}"
        holder.noteTextView.text = "Note: ${currentCME.note}"
        holder.catalogTextView.text = "Catalog: ${currentCME.catalog}"
        holder.linkTextView.text = "Link: ${currentCME.link}"
    }

    override fun getItemCount() = cmeList.size
}
