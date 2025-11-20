package com.example.tenizenapp

import android.app.DatePickerDialog
import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TambahProdukActivity : AppCompatActivity() {

    private lateinit var etID: EditText
    private lateinit var etNama: EditText
    private lateinit var etJumlah: EditText
    private lateinit var etHarga: EditText
    private lateinit var etTanggal: EditText
    private lateinit var btnSimpan: Button

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_produk)

        etID = findViewById(R.id.etID)
        etNama = findViewById(R.id.etNama)
        etJumlah = findViewById(R.id.etJumlah)
        etHarga = findViewById(R.id.etHarga)
        etTanggal = findViewById(R.id.etTanggal)
        btnSimpan = findViewById(R.id.btnSimpan)

        // DatePicker
        etTanggal.setOnClickListener { showDatePicker() }

        btnSimpan.setOnClickListener {
            if (validateInput()) addProduk()
        }
    }

    private fun showDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
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

    private fun validateInput(): Boolean {
        if (etID.text.isBlank()) { etID.error = "ID tidak boleh kosong"; return false }
        if (etNama.text.isBlank()) { etNama.error = "Nama tidak boleh kosong"; return false }
        if (etJumlah.text.isBlank()) { etJumlah.error = "Jumlah tidak boleh kosong"; return false }
        if (etHarga.text.isBlank()) { etHarga.error = "Harga tidak boleh kosong"; return false }
        if (etTanggal.text.isBlank()) { etTanggal.error = "Tanggal tidak boleh kosong"; return false }
        return true
    }

    private fun addProduk() {
        val json = JSONObject().apply {
            put("idproduk", etID.text.toString())
            put("namaproduk", etNama.text.toString())
            put("jumlah", etJumlah.text.toString())
            put("harga", etHarga.text.toString())
            put("tanggal", etTanggal.text.toString())
            put(
                "barcode",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Barcode128_Example.png/200px-Barcode128_Example.png"
            )
        }.toString()

        AddProdukTaskJSON(json).execute()
    }

    private inner class AddProdukTaskJSON(val json: String) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            val requestHandler = RequestHandler()
            return requestHandler.sendPostRequestJSON(Konfigurasi.URL_ADD_PRODUK, json)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (result.contains("berhasil", ignoreCase = true)) {
                Toast.makeText(this@TambahProdukActivity, "Berhasil menambahkan produk", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@TambahProdukActivity, "Gagal: $result", Toast.LENGTH_LONG).show()
            }
        }
    }
}
