package com.example.eyedentify;

import android.content.Intent
import android.media.Image
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
import android.util.Base64
import android.widget.Toast

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import android.graphics.Color


class Landing : AppCompatActivity() {

    private lateinit var rvCaptures: RecyclerView
    private lateinit var fabCamera: FloatingActionButton
    private lateinit var captureAdapter: CaptureAdapter
    private val captureList = mutableListOf<CaptureResult>()

    private var userID: Int = 1

    private var tierButtonsVisible = false
    private val tierButtons = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        userID = intent.getIntExtra("USER_ID", 11)
        val userTier = intent.getStringExtra("USER_TIER")

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
            if (userTier == "free") {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra("USER_ID", userID)
                startActivity(intent)
            }else {
                showTierButtons()
            }

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
        lifecycleScope.launch(Dispatchers.IO) {
            // Fetch data from Backend
            val historyItems = ApiTest.getUserHistory(userID)

            val newCaptures = mutableListOf<CaptureResult>()


            for (item in historyItems) {
                var uriString: String? = null

                if (!item.image_base64.isNullOrEmpty()) {
                    try {
                        val imageBytes = Base64.decode(item.image_base64, Base64.DEFAULT)

                        // Create a unique file name based on the name + random time
                        val fileName = "history_${System.currentTimeMillis()}_${item.name}.jpg"
                        val file = File(cacheDir, fileName)

                        FileOutputStream(file).use { output ->
                            output.write(imageBytes)
                        }
                        uriString = Uri.fromFile(file).toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                newCaptures.add(
                    CaptureResult(
                        imageResId = null,
                        imageUri = uriString,
                        name = item.name,
                        confidence = item.confidence,
                        description = item.description
                    )
                )
            }

            // Update UI
            withContext(Dispatchers.Main) {
                captureList.clear()
                captureList.addAll(newCaptures)
                captureAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun showTierButtons() {
        if (tierButtonsVisible) {
            tierButtons.forEach { it.visibility = View.GONE }
            tierButtonsVisible = false
            return
        }

        val parentLayout = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)

        val fabX = fabCamera.x + fabCamera.width / 2
        val fabY = fabCamera.y + fabCamera.height / 2

        val offsets = listOf(
            Pair(-170f, -150f), // top-left
            Pair(0f, -180f),    // top-center
            Pair(100f, -150f)   // top-right
        )

        offsets.forEachIndexed { index, (dx, dy) ->
            val btn = Button(this)
            btn.layoutParams = CoordinatorLayout.LayoutParams(120, 120)
            btn.background = getDrawable(R.drawable.round_button)
            btn.text = "O${index + 1}"
            btn.setTextColor(Color.WHITE)
            btn.x = fabX + dx - 60
            btn.y = fabY + dy - 60
            btn.elevation = 8f
            parentLayout.addView(btn)
            tierButtons.add(btn)
            btn.setOnClickListener {

                Toast.makeText(this, "Clicked option ${index + 1}", Toast.LENGTH_SHORT).show()
                hideTierButtons()
            }
        }


        tierButtonsVisible = true
    }

    private fun hideTierButtons() {
        tierButtons.forEach { it.visibility = View.GONE }
        tierButtonsVisible = false
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

        val tvDescription : TextView = view.findViewById(R.id.tvDescription)

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
        when {
            item.imageResId != null -> {
                // Load drawable
                Glide.with(holder.imageView.context)
                    .load(item.imageResId)
                    .centerCrop()
                    .into(holder.imageView)
            }
            !item.imageUri.isNullOrEmpty() -> {
                // Load file or URL
                Glide.with(holder.imageView.context)
                    .load(Uri.parse(item.imageUri))
                    .centerCrop()
                    .into(holder.imageView)
            }
            else -> {
                // placeholder if no image
                holder.imageView.setImageResource(R.drawable.imagenotfound)
            }
        }

        holder.tvName.text = item.name
        holder.tvConfidence.text = "Confidence: ${item.confidence}%"
        holder.tvDescription.text = item.description
    }

    override fun getItemCount(): Int = items.size
}


data class CaptureResult(
    val imageResId: Int?,
    val imageUri: String?,
    val name: String,
    val confidence: Double,
    val description: String
)

