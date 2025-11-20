package com.example.tenizenapp

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class RequestHandler {

    // --- KODE LAMA (synchronous) ---
    fun sendPostRequest(requestURL: String, postDataParams: HashMap<String, String>): String {
        var response = ""
        try {
            val conn = URL(requestURL).openConnection() as HttpURLConnection
            conn.readTimeout = 15000
            conn.connectTimeout = 15000
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.doInput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val os = conn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(getPostDataString(postDataParams))
            writer.flush()
            writer.close()
            os.close()

            val reader = if (conn.responseCode < HttpURLConnection.HTTP_BAD_REQUEST)
                BufferedReader(InputStreamReader(conn.inputStream))
            else
                BufferedReader(InputStreamReader(conn.errorStream))

            response = reader.readText()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
            response = "Exception: ${e.message}"
        }
        return response
    }

    fun sendPostRequestJSON(url: String, json: String): String {
        var response = ""
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.readTimeout = 15000
            conn.connectTimeout = 15000
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")

            conn.outputStream.use { os ->
                val writer = OutputStreamWriter(os, "UTF-8")
                writer.write(json)
                writer.flush()
                writer.close()
            }

            val reader = if (conn.responseCode < HttpURLConnection.HTTP_BAD_REQUEST)
                BufferedReader(InputStreamReader(conn.inputStream))
            else
                BufferedReader(InputStreamReader(conn.errorStream))

            response = reader.readText()
            reader.close()

        } catch (e: Exception) {
            e.printStackTrace()
            response = "Exception: ${e.message}"
        }
        return response
    }

    fun sendGetRequest(requestURL: String): String {
        var response = ""
        try {
            val conn = URL(requestURL).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            val reader = if (conn.responseCode < HttpURLConnection.HTTP_BAD_REQUEST)
                BufferedReader(InputStreamReader(conn.inputStream))
            else
                BufferedReader(InputStreamReader(conn.errorStream))

            response = reader.readText()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
            response = "Exception: ${e.message}"
        }
        return response
    }

    private fun getPostDataString(params: Map<String, String>): String {
        return params.entries.joinToString("&") { (k, v) ->
            "${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}"
        }
    }

    // --- KODE BARU (asynchronous) ---
    fun post(
        url: String,
        params: Map<String, String>,
        callback: (response: String?, error: String?) -> Unit
    ) {
        Thread {
            try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val os = conn.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                writer.write(getPostDataString(params))
                writer.flush()
                writer.close()
                os.close()

                val reader = if (conn.responseCode < HttpURLConnection.HTTP_BAD_REQUEST)
                    BufferedReader(InputStreamReader(conn.inputStream))
                else
                    BufferedReader(InputStreamReader(conn.errorStream))

                val response = reader.readText()
                reader.close()
                callback(response, null)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null, e.message)
            }
        }.start()
    }
}
