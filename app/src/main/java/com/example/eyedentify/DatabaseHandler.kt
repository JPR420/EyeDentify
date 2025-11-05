package com.example.eyedentify


import android.content.Context
import java.io.FileInputStream
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

object DatabaseHandler {
    private lateinit var DB_URL: String

    fun init(context: Context) {
        Env.load(context)
        DB_URL = Env.get("DATABASE_URL") ?: ""
        println("DB URL: $DB_URL")

        try {
            Class.forName("org.postgresql.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun getConnection(): Connection? {
        return try {
            val conn = DriverManager.getConnection(DB_URL)
            println("Database connected!") // <-- debug
            conn
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ✅ Register user
    fun registerUser(email: String, passwordHash: String, tier: String = "free"): Boolean {
        val sql = "INSERT INTO users (email, password_hash, tier) VALUES (?, ?, ?)"
        getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                stmt.setString(2, passwordHash)
                stmt.setString(3, tier)
                return stmt.executeUpdate() > 0
            }
        }
        return false
    }

    // Login user (returns tier if success, null if failure)
    fun loginUser(email: String, passwordHash: String): String? {
        val sql = "SELECT tier FROM users WHERE email = ? AND password_hash = ?"
        getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                stmt.setString(2, passwordHash)
                val rs: ResultSet = stmt.executeQuery()
                if (rs.next()) {
                    return rs.getString("tier")
                }
            }
        }
        return null
    }

    // check if email already exists
    fun isEmailRegistered(email: String): Boolean {
        val sql = "SELECT id FROM users WHERE email = ?"
        getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                val rs = stmt.executeQuery()
                return rs.next()
            }
        }
        return false
    }
}

object Env {
    private val props = Properties()

    fun load(context: Context) {
        try {
            val file = context.getFileStreamPath("local.properties")
            if (file.exists()) {
                FileInputStream(file).use { fis ->
                    props.load(fis)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun get(key: String): String? = props.getProperty(key)
}
