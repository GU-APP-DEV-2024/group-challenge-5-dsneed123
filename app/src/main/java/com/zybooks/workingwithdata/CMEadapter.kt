package com.zybooks.workingwithdata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class CME(val time: String, val latitude: Double, val longitude: Double, val speed: Int)

class CMEAdapter(private val cmeList: List<CME>) : RecyclerView.Adapter<CMEAdapter.CMEViewHolder>() {

    class CMEViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val latitudeTextView: TextView = itemView.findViewById(R.id.latitudeTextView)
        val longitudeTextView: TextView = itemView.findViewById(R.id.longitudeTextView)
        val speedTextView: TextView = itemView.findViewById(R.id.speedTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CMEViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cme_item, parent, false)
        return CMEViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CMEViewHolder, position: Int) {
        val currentCME = cmeList[position]
        holder.timeTextView.text = "Time: ${currentCME.time}"
        holder.latitudeTextView.text = "Latitude: ${currentCME.latitude}"
        holder.longitudeTextView.text = "Longitude: ${currentCME.longitude}"
        holder.speedTextView.text = "Speed: ${currentCME.speed} km/s"
    }

    override fun getItemCount() = cmeList.size
}
