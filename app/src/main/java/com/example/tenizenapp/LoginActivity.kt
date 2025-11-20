package com.example.tenizenapp

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                LoginUser().execute(email, password)
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private inner class LoginUser : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val email = params[0]
            val password = params[1]

            val requestHandler = RequestHandler()
            val postData = HashMap<String, String>()
            postData[Konfigurasi.KEY_EMAIL] = email
            postData[Konfigurasi.KEY_PASSWORD] = password

            return requestHandler.sendPostRequest(Konfigurasi.URL_LOGIN, postData)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                if (result.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Response kosong", Toast.LENGTH_SHORT).show()
                    return
                }
                val jsonObject = JSONObject(result)
                when (jsonObject.getString("status")) {
                    "sukses" -> {
                        val userData = jsonObject.getJSONObject("data")
                        Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()

                        // Simpan data user ke SharedPreferences
                        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putInt("user_id", userData.getInt("id"))
                            putString("username", userData.getString("username"))
                            putString("email", userData.getString("email"))
                            apply()
                        }

                        // Pindah ke MainActivity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}