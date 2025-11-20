package com.example.tenizenapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tenizenapp.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnProduk: Button
    private lateinit var btnSiswa: Button
    private lateinit var btnTransaksi: Button
    private lateinit var btnLogout: Button
    private lateinit var sharedPref: SharedPreferences

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 1. Ambil shared preferences untuk cek login
        sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val username = sharedPref.getString("username", null)

        // 2. Kalau belum login, lempar ke LoginActivity
//        if (username == null) {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//            return
//        }

        btnSiswa = findViewById(R.id.btnSiswa)
        btnProduk = findViewById(R.id.btnProduk)
        btnTransaksi = findViewById(R.id.btnTransaksi)
        btnLogout = findViewById(R.id.btnLogout)


        btnSiswa.setOnClickListener{
            startActivity(Intent(this,SiswaActivity::class.java))
        }

        // 3. Kalau sudah login, lanjut render UI



    }
}
