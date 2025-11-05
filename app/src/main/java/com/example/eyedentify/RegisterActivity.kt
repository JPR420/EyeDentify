package com.example.eyedentify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        DatabaseHandler.init(this);

        val etEmail = findViewById<EditText>(R.id.emailInput)
        val etPassword = findViewById<EditText>(R.id.passwordInput)
        val etConfirmPassword = findViewById<EditText>(R.id.passwordInputConfirm)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLogin)
        tvLoginLink.paint.isUnderlineText = true


        btnRegister.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            if (DatabaseHandler.isEmailRegistered(email)) {
//                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            if (DatabaseHandler.registerUser(email, password)) {
                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
