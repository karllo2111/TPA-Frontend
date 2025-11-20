package com.example.tenizenapp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class VerifyOtpActivity : AppCompatActivity() {

    private lateinit var etOtp: EditText
    private lateinit var btnVerify: Button
    private lateinit var tvResend: TextView
    private lateinit var tvInfo2: TextView
    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        tvInfo2 = findViewById(R.id.tvInfo2)
        etOtp = findViewById(R.id.etOtp)
        btnVerify = findViewById(R.id.btnVerify)
        tvResend = findViewById(R.id.tvResend)

        userEmail = intent.getStringExtra("email") ?: ""

        btnVerify.setOnClickListener {
            val otp = etOtp.text.toString()
            if (otp.isEmpty()) {
                Toast.makeText(this, "OTP harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                VerifyOtpTask().execute(userEmail, otp)
            }
        }

        tvResend.setOnClickListener {
            tvResend.isEnabled = false
            ResendOtpTask().execute(userEmail)

            object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    tvResend.text = "Kirim ulang (${millisUntilFinished / 1000}s)"
                }
                override fun onFinish() {
                    tvResend.isEnabled = true
                    tvResend.text = "Kirim ulang OTP"
                }
            }.start()
        }
    }

    private inner class VerifyOtpTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val email = params[0]
            val otp = params[1]
            val requestHandler = RequestHandler()
            val postData = JSONObject().apply {
                put(Konfigurasi.KEY_EMAIL, email)
                put(Konfigurasi.KEY_OTP, otp)
            }.toString()
            return requestHandler.sendPostRequestJSON(Konfigurasi.URL_VERIFY_OTP, postData)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                when (jsonObject.getString(Konfigurasi.TAG_STATUS)) {
                    "success" -> {
                        Toast.makeText(this@VerifyOtpActivity, "OTP valid!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@VerifyOtpActivity, LoginActivity::class.java))
                        finish()
                    }
                    "error" -> {
                        Toast.makeText(this@VerifyOtpActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@VerifyOtpActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private inner class ResendOtpTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val email = params[0]
            val requestHandler = RequestHandler()
            val postData = JSONObject().apply {
                put(Konfigurasi.KEY_EMAIL, email)
            }.toString()
            return requestHandler.sendPostRequestJSON(Konfigurasi.URL_RESEND_OTP, postData)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                when (jsonObject.getString(Konfigurasi.TAG_STATUS)) {
                    "success" -> {
                        val otp = jsonObject.getString("otp")
                        tvInfo2.text = "Kode OTP: $otp"
                    }
                    "error" -> {
                        tvInfo2.text = "Error: ${jsonObject.getString("message")}"
                    }
                }
            } catch (e: Exception) {
                tvInfo2.text = "Error: ${e.message}"
            }
        }
    }
}
