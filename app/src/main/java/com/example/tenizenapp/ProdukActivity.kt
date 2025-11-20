package com.example.tenizenapp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ProdukActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var etSearch: EditText
    private lateinit var btnTambah: Button
    private lateinit var btnRefresh: Button
    private var dataProduk = mutableListOf<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produk)

        tableLayout = findViewById(R.id.tableLayout)
        btnTambah = findViewById(R.id.btnTambah)
        btnRefresh = findViewById(R.id.btnRefresh)
        etSearch = findViewById(R.id.etSearch)

        btnTambah.setOnClickListener {
            startActivity(Intent(this, TambahProdukActivity::class.java))
        }

        btnRefresh.setOnClickListener {
            loadProduk()
        }

        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                filterProduk(s.toString().lowercase())
            }
        })
    }

    private fun filterProduk(keyword: String) {
        tableLayout.removeAllViews()
        for (obj in dataProduk) {
            val id = obj.getString("idproduk")
            val name = obj.getString("name")
            val jumlah = obj.getString("jumlah")
            val harga = obj.getString("harga")
            val tanggal = obj.getString("tanggal")

            if (id.lowercase().contains(keyword) || name.lowercase().contains(keyword)) {
                val row = TableRow(this).apply {
                    setPadding(0, 8, 0, 8)
                    isClickable = true
                    setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
                }

                listOf(id, name, jumlah, harga, tanggal).forEach { value ->
                    val tv = TextView(this)
                    tv.text = value
                    tv.setPadding(8, 4, 8, 4)
                    row.addView(tv)
                }

                row.setOnClickListener { showDetailDialog(obj) }
                tableLayout.addView(row)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        loadProduk() // Auto refresh saat kembali dari tambah/update
    }

    private fun loadProduk() {
        LoadProdukTask().execute()
    }

    private inner class LoadProdukTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            val requestHandler = RequestHandler()
            return requestHandler.sendGetRequest(Konfigurasi.URL_GET_ALL_PRODUK)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                val produkArray = jsonObject.getJSONArray("result")

                dataProduk.clear()
                for (i in 0 until produkArray.length()) {
                    dataProduk.add(produkArray.getJSONObject(i))
                }

                displayProduk()
            } catch (e: Exception) {
                Toast.makeText(this@ProdukActivity, "Error loading data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayProduk() {
        tableLayout.removeAllViews() // Hapus semua baris sebelumnya

        for (obj in dataProduk) {
            val row = TableRow(this).apply {
                setPadding(0, 8, 0, 8)
                isClickable = true
                setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
            }

            val id = obj.getString("idproduk")
            val name = obj.getString("name")
            val jumlah = obj.getString("jumlah")
            val harga = obj.getString("harga")
            val tanggal = obj.getString("tanggal")

            listOf(id, name, jumlah, harga, tanggal).forEach { value ->
                val tv = TextView(this)
                tv.text = value
                tv.setPadding(8, 4, 8, 4)
                row.addView(tv)
            }

            row.setOnClickListener { showDetailDialog(obj) }
            tableLayout.addView(row)
        }
    }

    private fun showDetailDialog(produk: JSONObject) {
        val dialog = android.app.AlertDialog.Builder(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        listOf(
            "ID: ${produk.getString("idproduk")}",
            "Nama: ${produk.getString("name")}",
            "Jumlah: ${produk.getString("jumlah")}",
            "Harga: ${produk.getString("harga")}",
            "Tanggal: ${produk.getString("tanggal")}"
        ).forEach {
            layout.addView(TextView(this).apply { text = it; setPadding(0, 8, 0, 8) })
        }

        dialog.setView(layout)
            .setPositiveButton("Edit") { _, _ ->
                val intent = Intent(this, EditProdukActivity::class.java)
                intent.putExtra("produk_data", produk.toString())
                startActivity(intent)
            }
            .setNegativeButton("Hapus") { _, _ ->
                HapusProdukTask(produk.getString("idproduk")).execute()
            }
            .setNeutralButton("Tutup", null)
            .show()
    }

    private inner class HapusProdukTask(val idproduk: String) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            val requestHandler = RequestHandler()
            val postData = HashMap<String,String>()
            postData["idproduk"] = idproduk
            return requestHandler.sendPostRequest(Konfigurasi.URL_DELETE_PRODUK, postData)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Toast.makeText(this@ProdukActivity, result, Toast.LENGTH_SHORT).show()
            loadProduk() // refresh setelah hapus
        }
    }
}
