package com.example.eyedentify;

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Landing : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvUserInfo = findViewById<TextView>(R.id.tvUserInfo)

        val userId = intent.getIntExtra("USER_ID", -1)
        val userTier = intent.getStringExtra("USER_TIER") ?: "Unknown"

        tvWelcome.text = "Welcome!"
        tvUserInfo.text = "Your ID: $userId\nTier: $userTier"


    }
}
