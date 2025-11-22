package com.example.eyedentify


import android.R
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.gson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.io.readBytes
import android.media.Image
import android.util.Log

@Serializable
data class LoginResponse(val id: Int? = null, val tier: String? = null)
@Serializable
data class IdentifyResponse(val name: String, val confidence: String, val description: String, val link : String? = null)
@Serializable
data class UserImagesResponse(val file : IdentifyResponse, val image : ByteArray)
@Serializable
data class HistoryResponseItem(val name: String,val confidence: Double,val description: String,val link: String?,val image_base64: String?)
object ApiTest {

//    private const val BASE_URL = "http://10.0.2.2:8080"
    private const val BASE_URL = "http://192.168.1.160:8080"



    suspend fun register(email: String, password: String) : Boolean {


        try{

            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    gson()
                }
            }

            client.submitForm(
                url = "$BASE_URL/register",
                formParameters = Parameters.build {
                    append("email", email)
                    append("password", password)
                }
            )
            client.close()
        }catch (e: Exception){
            return false
        }
        return true;
    }


    suspend fun login(email: String, password: String): LoginResponse? {

        return try {

            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    gson()
                }
            }

            val response: HttpResponse = client.submitForm(
                url = "$BASE_URL/login",
                formParameters = Parameters.build {
                    append("email", email)
                    append("password", password)
                }
            )
            val bodyText = response.bodyAsText()
            client.close()

            Json.decodeFromString<LoginResponse>(bodyText)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun identifyImage(file: File): IdentifyResponse? {
        return try {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    gson()
                }
            }

            Log.d("API_TEST", "Sending request to /identify")

            println("File size: ${file.length()} bytes")
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = "$BASE_URL/identify",
                formData = formData {
                    append(
                        "image",
                        file.readBytes(),
                        Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"upload.jpg\"")
                        }
                    )
                }
            )

            val bodyText = response.bodyAsText()
            client.close()
            
            Log.d("API_TEST", "body after identifying picture: $bodyText")

            Json.decodeFromString<IdentifyResponse>(bodyText)



        } catch (e: Exception) {
            Log.e("API_TEST", "Exception sending image: ${e.message}", e)
            null
        }
    }

    suspend fun saveResult(imageBytes: ByteArray?,objectName: String,confidence: String,description: String,buyLink: String? = null, userId: Int): Boolean {

        if (imageBytes == null)
            return false

        val client = HttpClient(CIO) {
            install(ContentNegotiation) { gson() }
        }

        try {
            client.submitFormWithBinaryData(
                url = "$BASE_URL/saveResult",
                formData = formData {
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"capture.jpg\"")
                    })
                    append("object_name", objectName)
                    append("confidence", confidence)
                    append("description", description)
                    buyLink?.let { append("buy_link", it) }
                    append("user_id", userId.toString())
                }
            )
            client.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }


    suspend fun getUserHistory(userId: Int): List<HistoryResponseItem> {
        return try {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) { gson() }
            }
            val response: HttpResponse = client.submitForm(
                url = "$BASE_URL/getUserHistory",
                formParameters = Parameters.build {
                    append("user_id", userId.toString())
                }
            )

            val bodyText = response.bodyAsText()
            client.close()
            Json.decodeFromString<List<HistoryResponseItem>>(bodyText)

        } catch (e: Exception) {
            Log.e("API_TEST", "Error fetching history: ${e.message}")
            emptyList()
        }
    }
}










