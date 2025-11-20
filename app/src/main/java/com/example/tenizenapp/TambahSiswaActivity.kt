package com.example.tenizenapp

import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TambahSiswaActivity : AppCompatActivity() {

    private lateinit var etNIS: EditText
    private lateinit var etNama: EditText
    private lateinit var etGender: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etTglLahir: EditText
    private lateinit var btnSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_siswa)

        etNIS = findViewById(R.id.etNIS)
        etNama = findViewById(R.id.etNama)
        etGender = findViewById(R.id.etGender)
        etAlamat = findViewById(R.id.etAlamat)
        etTglLahir = findViewById(R.id.etTglLahir)
        btnSimpan = findViewById(R.id.btnSimpan)

        btnSimpan.setOnClickListener {
            if (validateInput()) {
                tambahSiswa()
            }
        }
    }

    private fun validateInput(): Boolean {
        if (etNIS.text.isBlank()) {
            etNIS.error = "NIS tidak boleh kosong"
            return false
        }
        if (etNama.text.isBlank()) {
            etNama.error = "Nama tidak boleh kosong"
            return false
        }
        if (etGender.text.isBlank()) {
            etGender.error = "Gender tidak boleh kosong"
            return false
        }
        if (etAlamat.text.isBlank()) {
            etAlamat.error = "Alamat tidak boleh kosong"
            return false
        }
        if (etTglLahir.text.isBlank()) {
            etTglLahir.error = "Tanggal lahir tidak boleh kosong"
            return false
        }
        return true
    }

    private fun tambahSiswa() {
        val nis = etNIS.text.toString()
        val name = etNama.text.toString()
        val gender = etGender.text.toString()
        val alamat = etAlamat.text.toString()
        val tgllahir = etTglLahir.text.toString()

        TambahSiswaTask(nis, name, gender, alamat, tgllahir).execute()
    }

    private inner class TambahSiswaTask(
        val nis: String,
        val name: String,
        val gender: String,
        val alamat: String,
        val tgllahir: String
    ) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            val postData = HashMap<String, String>()
            postData["nis"] = nis
            postData["namasiswa"] = name
            postData["gender"] = gender
            postData["alamat"] = alamat
            postData["tanggallahir"] = tgllahir

            val requestHandler = RequestHandler()
            return requestHandler.sendPostRequest(Konfigurasi.URL_ADD_SISWA, postData)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (result.contains("berhasil", ignoreCase = true)) {
                Toast.makeText(this@TambahSiswaActivity, "Siswa berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                finish() // kembali ke SiswaActivity
            } else {
                Toast.makeText(this@TambahSiswaActivity, "Gagal menambahkan: $result", Toast.LENGTH_LONG).show()
            }
        }
    }
}
