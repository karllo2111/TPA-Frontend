package com.example.tenizenapp

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class RequestHandler {
    fun sendPostRequest(requestURL: String, postDataParams: HashMap<String, String>): String {
        val url: URL
        var response = ""
        try {
            url = URL(requestURL)
            val conn = url.openConnection() as HttpURLConnection
            conn.readTimeout = 15000
            conn.connectTimeout = 15000
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true

            val os = conn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(getPostDataString(postDataParams))
            writer.flush()
            writer.close()
            os.close()

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                response = br.readText()
            } else {
                response = "Error: $responseCode"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    fun sendGetRequest(requestURL: String): String {
        val sb = StringBuilder()
        try {
            val url = URL(requestURL)
            val conn = url.openConnection() as HttpURLConnection
            val inputStream = BufferedInputStream(conn.inputStream)
            val br = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    private fun getPostDataString(params: HashMap<String, String>): String {
        val result = StringBuilder()
        var first = true
        for ((key, value) in params) {
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }
        return result.toString()
    }
}