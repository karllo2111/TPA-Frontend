package com.example.tenizenapp

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject

class VerifyOtpActivity : AppCompatActivity() {
    private lateinit var etOtp: EditText
    private lateinit var btnVerify: Button
    private lateinit var tvResend: TextView
    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        etOtp = findViewById(R.id.etOtp)
        btnVerify = findViewById(R.id.btnVerify)
        tvResend = findViewById(R.id.tvResend)

        userEmail = intent.getStringExtra("email") ?: ""

        btnVerify.setOnClickListener {
            val otp = etOtp.text.toString()
            if (otp.isEmpty()) {
                Toast.makeText(this, "OTP harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                VerifyOtp().execute(userEmail, otp)
            }
        }

        tvResend.setOnClickListener {
            // Logic untuk kirim ulang OTP bisa ditambahkan di sini
            Toast.makeText(this, "Fitur resend OTP belum implement", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class VerifyOtp : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val email = params[0]
            val otp = params[1]

            val requestHandler = RequestHandler()
            val postData = HashMap<String, String>()
            postData[Konfigurasi.KEY_EMAIL] = email
            postData[Konfigurasi.KEY_OTP] = otp

            return requestHandler.sendPostRequest(Konfigurasi.URL_VERIFY_OTP, postData)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                when (jsonObject.getString(Konfigurasi.TAG_STATUS)) {
                    "success" -> {
                        Toast.makeText(this@VerifyOtpActivity, "Verifikasi berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@VerifyOtpActivity, LoginActivity::class.java))
                        finish()
                    }
                    "error" -> {
                        Toast.makeText(this@VerifyOtpActivity, jsonObject.getString(Konfigurasi.TAG_MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@VerifyOtpActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}