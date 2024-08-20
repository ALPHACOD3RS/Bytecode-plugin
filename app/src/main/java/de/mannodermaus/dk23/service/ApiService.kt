package de.mannodermaus.dk23.service

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object ApiService {

    fun makeApiCall(callback: ApiCallback) {
        AsyncTask.execute {
            val urlString = "https://shop-fxrs.onrender.com/project-x"
            val url = URL(urlString)
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"

//                connection.setRequestProperty("Authorization", "siu")

                Log.i("ApiService", "Making API Call:")
                Log.i("ApiService", "Request URL: $urlString")
                Log.i("ApiService", "Request Method: ${connection.requestMethod}")

                val headers = connection.requestProperties

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    callback.onFailure("HTTP Error Code: $responseCode")
                    return@execute
                }

                // Read the response
                val inputStream = connection.inputStream
                reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                callback.onSuccess(response.toString(), headers.toString())

            } catch (e: Exception) {
                Log.e("ApiService", "Error during API call", e)
                callback.onFailure(e.message ?: "Unknown error")
            } finally {
                reader?.close()
                connection?.disconnect()
            }
        }
    }

    interface ApiCallback {
        fun onSuccess(response: String, headers: String)
        fun onFailure(errorMessage: String)
    }
}
