package com.example.tenizenapp

import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class EditSiswaActivity : AppCompatActivity() {

    private lateinit var etNIS: EditText
    private lateinit var etNama: EditText
    private lateinit var etGender: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etTglLahir: EditText
    private lateinit var btnSimpan: Button

    private lateinit var siswaData: JSONObject
    private var fotoBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_siswa)

        etNIS = findViewById(R.id.etNIS)
        etNama = findViewById(R.id.etNama)
        etGender = findViewById(R.id.etGender)
        etAlamat = findViewById(R.id.etAlamat)
        etTglLahir = findViewById(R.id.etTglLahir)
        btnSimpan = findViewById(R.id.btnSimpan)

        // Ambil data dari Intent
        val dataString = intent.getStringExtra("siswa_data")
        if (dataString != null) {
            siswaData = JSONObject(dataString)
            loadData()
        } else {
            Toast.makeText(this, "Data siswa tidak ditemukan!", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnSimpan.setOnClickListener {
            if (validateInput()) updateSiswa()
        }
    }

    private fun loadData() {
        etNIS.setText(siswaData.getString("nis"))
        etNIS.isEnabled = false
        etNama.setText(siswaData.getString("name"))
        etGender.setText(siswaData.getString("gender"))
        etAlamat.setText(siswaData.getString("alamat"))
        etTglLahir.setText(siswaData.getString("tanggallahir"))
        fotoBase64 = siswaData.optString("foto", "")
    }

    private fun validateInput(): Boolean {
        if (etNama.text.isBlank()) { etNama.error = "Nama tidak boleh kosong"; return false }
        if (etGender.text.isBlank()) { etGender.error = "Gender tidak boleh kosong"; return false }
        if (etAlamat.text.isBlank()) { etAlamat.error = "Alamat tidak boleh kosong"; return false }
        if (etTglLahir.text.isBlank()) { etTglLahir.error = "Tanggal lahir tidak boleh kosong"; return false }
        return true
    }

    private fun updateSiswa() {
        val postData = HashMap<String, String>()
        postData["nis"] = etNIS.text.toString()
        postData["name"] = etNama.text.toString()       // key sesuai PHP
        postData["gender"] = etGender.text.toString()
        postData["alamat"] = etAlamat.text.toString()
        postData["tanggallahir"] = etTglLahir.text.toString()
        fotoBase64?.takeIf { it.isNotEmpty() }?.let { postData["foto"] = it }

        UpdateSiswaTask(postData).execute()
    }


    private inner class UpdateSiswaTask(val postData: HashMap<String,String>) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            val requestHandler = RequestHandler()
            return requestHandler.sendPostRequest(Konfigurasi.URL_UPDATE_SISWA, postData)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (result.contains("berhasil", ignoreCase = true)) {
                Toast.makeText(this@EditSiswaActivity, "Update berhasil!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this@EditSiswaActivity, "Update gagal: $result", Toast.LENGTH_LONG).show()
            }
        }
    }
}
