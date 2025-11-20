package com.example.tenizenapp

import android.app.DatePickerDialog
import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONObject

class EditProdukActivity : AppCompatActivity() {

    private lateinit var etID: EditText
    private lateinit var etNama: EditText
    private lateinit var etJumlah: EditText
    private lateinit var etHarga: EditText
    private lateinit var etTanggal: EditText
    private lateinit var btnSimpan: Button

    private lateinit var produkData: HashMap<String, String>
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_produk)

        etID = findViewById(R.id.etID)
        etNama = findViewById(R.id.etNama)
        etJumlah = findViewById(R.id.etJumlah)
        etHarga = findViewById(R.id.etHarga)
        etTanggal = findViewById(R.id.etTanggal)
        btnSimpan = findViewById(R.id.btnSimpan)

        // Ambil data dari Intent (JSON string)
        val dataString = intent.getStringExtra("produk_data")
        if (dataString != null) {
            val json = JSONObject(dataString)
            produkData = HashMap()
            produkData["idproduk"] = json.getString("idproduk")
            produkData["name"] = json.getString("name")
            produkData["jumlah"] = json.getString("jumlah")
            produkData["harga"] = json.getString("harga")
            produkData["tanggal"] = json.getString("tanggal")
            produkData["barcode"] = json.getString("barcode")
        } else {
            Toast.makeText(this, "Data produk tidak ditemukan!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadData()
        etTanggal.setOnClickListener { showDatePicker() }
        btnSimpan.setOnClickListener {
            if (validateInput()) updateProduk()
        }
    }


    private fun loadData() {
        etID.setText(produkData["idproduk"])
        etNama.setText(produkData["name"])
        etJumlah.setText(produkData["jumlah"])
        etHarga.setText(produkData["harga"])
        etTanggal.setText(produkData["tanggal"])
    }

    private fun validateInput(): Boolean {
        if (etNama.text.isBlank()) { etNama.error = "Nama tidak boleh kosong"; return false }
        if (etJumlah.text.isBlank()) { etJumlah.error = "Jumlah tidak boleh kosong"; return false }
        if (etHarga.text.isBlank()) { etHarga.error = "Harga tidak boleh kosong"; return false }
        if (etTanggal.text.isBlank()) { etTanggal.error = "Tanggal tidak boleh kosong"; return false }
        return true
    }

    private fun showDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            etTanggal.setText(sdf.format(calendar.time))
        }

        DatePickerDialog(
            this,
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateProduk() {
        val postData = HashMap<String, String>()
        postData["idproduk"] = etID.text.toString()
        postData["namaproduk"] = etNama.text.toString()
        postData["jumlah"] = etJumlah.text.toString()
        postData["harga"] = etHarga.text.toString()
        postData["tanggal"] = etTanggal.text.toString()
        postData["barcode"] = produkData["barcode"] ?: ""  // tetap pakai barcode lama

        UpdateProdukTask(postData).execute()
    }

    private inner class UpdateProdukTask(val postData: HashMap<String,String>) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            val requestHandler = RequestHandler()
            return requestHandler.sendPostRequest(Konfigurasi.URL_UPDATE_PRODUK, postData)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (result.contains("berhasil", ignoreCase = true)) {
                Toast.makeText(this@EditProdukActivity, "Update berhasil!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@EditProdukActivity, "Update gagal: $result", Toast.LENGTH_LONG).show()
            }
        }
    }
}
