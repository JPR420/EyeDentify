package com.example.eyedentify


import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@Serializable
data class LoginResponse(val id: Int? = null, val tier: String? = null)

object ApiTest {

//    private const val BASE_URL = "http://10.0.2.2:8080"
    private const val BASE_URL = "http://192.168.1.160:8080"

    suspend fun register(email: String, password: String) : Boolean {
        try{
            val client = HttpClient(CIO)
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
            val client = HttpClient(CIO)
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





}

