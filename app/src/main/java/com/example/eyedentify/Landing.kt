package com.example.eyedentify;

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.ImageButton


class Landing : AppCompatActivity() {

    private lateinit var rvCaptures: RecyclerView
    private lateinit var fabCamera: FloatingActionButton
    private lateinit var captureAdapter: CaptureAdapter
    private val captureList = mutableListOf<CaptureResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        rvCaptures = findViewById(R.id.rvCaptures)

        fabCamera = findViewById(R.id.fabCamera)

        captureAdapter = CaptureAdapter(captureList) { capture ->

            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("imageUri", capture.imageUri)
            startActivity(intent)
        }

        rvCaptures.layoutManager = LinearLayoutManager(this)
        rvCaptures.adapter = captureAdapter

        //Setting Button
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            val intent = Intent(this,SettingActivity::class.java)
            startActivity(intent)
        }

        //Camera button
        fabCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        //Subscription button
        val btnSubscription = findViewById<ImageButton>(R.id.btnSubscription)
        btnSubscription.setOnClickListener {
            val intent = Intent(this, SubscriptionActivity::class.java)
            startActivity(intent)
        }

        loadHistory()
    }

    private fun loadHistory() {

        // Example data:
        captureList.add(
            CaptureResult(
                imageUri = "file:///storage/emulated/0/DCIM/capture1.jpg",
                name = "Rose",
                confidence = 0.92f,
                description = "A red flower commonly found in gardens."
            )
        )
        captureList.add(
            CaptureResult(
                imageUri = "file:///storage/emulated/0/DCIM/capture1.jpg",
                name = "Rose",
                confidence = 0.92f,
                description = "A red flower commonly found in gardens."
            )
        )
        captureList.add(
            CaptureResult(
                imageUri = "file:///storage/emulated/0/DCIM/capture1.jpg",
                name = "Rose",
                confidence = 0.92f,
                description = "A red flower commonly found in gardens."
            )
        )
        captureList.add(
            CaptureResult(
                imageUri = "file:///storage/emulated/0/DCIM/capture1.jpg",
                name = "Rose",
                confidence = 0.92f,
                description = "A red flower commonly found in gardens."
            )
        )
        captureAdapter.notifyDataSetChanged()
    }

}

class CaptureAdapter(
    private val items: List<CaptureResult>,
    private val onItemClick: (CaptureResult) -> Unit
) : RecyclerView.Adapter<CaptureAdapter.CaptureViewHolder>() {

    inner class CaptureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.ivCapture)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvConfidence: TextView = view.findViewById(R.id.tvConfidence)

        init {
            view.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaptureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_capture, parent, false)
        return CaptureViewHolder(view)
    }

    override fun onBindViewHolder(holder: CaptureViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.imageView.context)
            .load(Uri.parse(item.imageUri))
            .centerCrop()
            .into(holder.imageView)

        holder.tvName.text = item.name
        holder.tvConfidence.text = "Confidence: ${(item.confidence * 100).toInt()}%"
    }

    override fun getItemCount(): Int = items.size
}


data class CaptureResult(
    val imageUri: String,
    val name: String,
    val confidence: Float,
    val description: String
)

