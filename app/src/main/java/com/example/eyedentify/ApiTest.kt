package com.example.eyedentify


import android.util.Log
import com.google.gson.JsonObject
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.gson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.File

@Serializable
data class LoginResponse(val id: Int? = null, val tier: String? = null)
@Serializable
data class IdentifyResponse(val name: String, val confidence: String, val description: String)

object ApiTest {

//    private const val BASE_URL = "http://10.0.2.2:8080"
    private const val BASE_URL = "http://192.168.1.160:8080"

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun register(email: String, password: String) : Boolean {
        try{

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

            Json.decodeFromString<IdentifyResponse>(bodyText)


        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }








}

