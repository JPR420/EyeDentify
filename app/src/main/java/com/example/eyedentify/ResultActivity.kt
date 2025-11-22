package com.example.eyedentify

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class ResultActivity : AppCompatActivity() {
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.analized_result)

         val appContext : Context = this

         val userID = intent.getIntExtra("USER_ID", 1)

         val imageUri = intent.getStringExtra("imageUri")
         val name = intent.getStringExtra("name")
         val confidence = intent.getStringExtra("confidence")
         val description = intent.getStringExtra("description")
         val link = intent.getStringExtra("link")

         val ivCapture = findViewById<ImageView>(R.id.ivCapture)
         val tvName = findViewById<TextView>(R.id.tvName)
         val tvConfidence = findViewById<TextView>(R.id.tvConfidence)
         val tvDescription = findViewById<TextView>(R.id.tvDescription)
         val saveBtn = findViewById<Button>(R.id.btnSave)
         val  goBackBtn = findViewById<Button>(R.id.btnGoBack)



         ivCapture.setImageURI(Uri.parse(imageUri))
         tvName.text = name
         tvConfidence.text = confidence
         tvDescription.text = description



         // Save Button
         saveBtn.setOnClickListener {
             lifecycleScope.launch {


                 if (imageUri == null || name == null || description == null || confidence == null) {
                     Toast.makeText(appContext, "Missing data, cannot save.", Toast.LENGTH_SHORT).show()
                     return@launch
                 }

                 val imageBytes = uriToBytes(Uri.parse(imageUri))

                 if (imageBytes == null) {
                     Toast.makeText(appContext, "Failed to convert image.", Toast.LENGTH_SHORT).show()
                     return@launch
                 }

                 val success = ApiTest.saveResult(
                     imageBytes,
                     name,
                     confidence,
                     description,
                     link,
                     userID
                 )

                 if (success)
                     Toast.makeText(appContext, "Saved!", Toast.LENGTH_SHORT).show()
                 else
                     Toast.makeText(appContext, "Save failed.", Toast.LENGTH_SHORT).show()

             }

         }


         // Go Back Button
         goBackBtn.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
             intent.putExtra("USER_ID", userID)
             startActivity(intent)
         }

    }

    private fun uriToBytes(uri: Uri?): ByteArray? {
        if (uri == null) return null
        return try {
            contentResolver.openInputStream(uri)?.readBytes()
        } catch (e: Exception) {
            null
        }
    }

}
