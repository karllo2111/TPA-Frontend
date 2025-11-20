package com.example.tenizenapp

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import org.json.JSONObject

class SiswaActivity : AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var etSearch: EditText
    private lateinit var btnTambah: Button
    private var dataSiswa = mutableListOf<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siswa)

        tableLayout = findViewById(R.id.tableLayout)
        etSearch = findViewById(R.id.etSearch)
        btnTambah = findViewById(R.id.btnTambah)

        btnTambah.setOnClickListener {
            startActivity(Intent(this, TambahSiswaActivity::class.java))
        }

        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                filterDataSiswa(s.toString().lowercase())
            }
        })
    }

    override fun onResume() {
        super.onResume()
        LoadSiswa().execute() // Refresh otomatis saat kembali ke activity
    }

    private inner class LoadSiswa : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            val requestHandler = RequestHandler()
            return requestHandler.sendGetRequest(Konfigurasi.URL_GET_ALL_SISWA)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                val siswaArray = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY)

                dataSiswa.clear()
                for (i in 0 until siswaArray.length()) {
                    dataSiswa.add(siswaArray.getJSONObject(i))
                }

                filterDataSiswa(etSearch.text.toString().lowercase())
            } catch (e: Exception) {
                Toast.makeText(this@SiswaActivity, "Error loading data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterDataSiswa(keyword: String) {
        val childCount = tableLayout.childCount
        if (childCount > 1) tableLayout.removeViews(1, childCount - 1)

        for (obj in dataSiswa) {
            val nis = obj.getString("nis")
            val name = obj.getString("name")
            val tgllahir = obj.getString("tanggallahir")

            if (nis.lowercase().contains(keyword) || name.lowercase().contains(keyword)) {
                val row = TableRow(this).apply {
                    setPadding(0, 8, 0, 8)
                    isClickable = true
                    setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
                }

                listOf(
                    TextView(this).apply { text = nis },
                    TextView(this).apply { text = name },
                    TextView(this).apply { text = tgllahir }
                ).forEach { tv ->
                    tv.setPadding(8, 4, 8, 4)
                    row.addView(tv)
                }

                row.setOnClickListener { showDetailDialog(obj) }
                tableLayout.addView(row)
            }
        }
    }

    private fun showDetailDialog(siswa: JSONObject) {
        val dialog = android.app.AlertDialog.Builder(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val nis = siswa.getString("nis")
        val name = siswa.getString("name")
        val gender = siswa.getString("gender")
        val alamat = siswa.getString("alamat")
        val tgllahir = siswa.getString("tanggallahir")

        listOf(
            "NIS: $nis",
            "Nama: $name",
            "Gender: $gender",
            "Alamat: $alamat",
            "Tgl Lahir: $tgllahir"
        ).forEach { text ->
            layout.addView(TextView(this).apply {
                this.text = text
                setPadding(0, 8, 0, 8)
            })
        }

        dialog.setView(layout)
            .setPositiveButton("Edit") { _, _ ->
                val intent = Intent(this, EditSiswaActivity::class.java)
                intent.putExtra("siswa_data", siswa.toString())
                startActivity(intent)
            }
            .setNegativeButton("Hapus") { _, _ ->
                HapusSiswa().execute(nis)
            }
            .setNeutralButton("Tutup", null)
            .show()
    }

    private inner class HapusSiswa : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val nis = params[0]
            val requestHandler = RequestHandler()
            val postData = HashMap<String, String>()
            postData[Konfigurasi.KEY_NIS] = nis
            return requestHandler.sendPostRequest(Konfigurasi.URL_DELETE_SISWA, postData)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Toast.makeText(this@SiswaActivity, result, Toast.LENGTH_SHORT).show()
            LoadSiswa().execute() // Refresh setelah hapus
        }
    }
}
