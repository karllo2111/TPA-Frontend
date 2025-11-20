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

class RegisterActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                RegisterUser().execute(username, email, password)
            }
        }
    }

    private inner class RegisterUser : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val username = params[0]
            val email = params[1]
            val password = params[2]

            val requestHandler = RequestHandler()
            val postData = HashMap<String, String>()
            postData[Konfigurasi.KEY_USERNAME] = username
            postData[Konfigurasi.KEY_EMAIL] = email
            postData[Konfigurasi.KEY_PASSWORD] = password

            return requestHandler.sendPostRequest(Konfigurasi.URL_REGISTER, postData)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                when (jsonObject.getString(Konfigurasi.TAG_STATUS)) {
                    "success" -> {
                        val otp = jsonObject.getString(Konfigurasi.TAG_OTP)
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil. OTP: $otp", Toast.LENGTH_LONG).show()

                        // Pindah ke OTP verification
                        val intent = Intent(this@RegisterActivity, VerifyOtpActivity::class.java).apply {
                            putExtra("email", etEmail.text.toString())
                        }
                        startActivity(intent)
                        finish()
                    }
                    "error" -> {
                        Toast.makeText(this@RegisterActivity, jsonObject.getString(Konfigurasi.TAG_MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}