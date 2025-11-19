package com.example.eyedentify

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_capture)

         val imageUri = intent.getStringExtra("imageUri")
         val name = intent.getStringExtra("name")
         val confidence = intent.getStringExtra("confidence")
         val description = intent.getStringExtra("description")

         val ivCapture = findViewById<ImageView>(R.id.ivCapture)
         val tvName = findViewById<TextView>(R.id.tvName)
         val tvConfidence = findViewById<TextView>(R.id.tvConfidence)
         val tvDescription = findViewById<TextView>(R.id.tvDescription)

         ivCapture.setImageURI(Uri.parse(imageUri))
         tvName.text = name
         tvConfidence.text = confidence
         tvDescription.text = description

    }
}
