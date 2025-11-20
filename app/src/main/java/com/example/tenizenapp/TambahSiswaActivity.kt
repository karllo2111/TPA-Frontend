package com.example.tenizenapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class TambahSiswaActivity : AppCompatActivity() {

    private lateinit var etNis: EditText
    private lateinit var etNama: EditText
    private lateinit var spGender: Spinner
    private lateinit var etAlamat: EditText
    private lateinit var etTgLahir: EditText
    private lateinit var etFoto: EditText
    private lateinit var btnSimpan: Button

    private var selectedGender: String = ""
    private val calendar = Calendar.getInstance()
    private val requestHandler = RequestHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_siswa)

        etNis = findViewById(R.id.etNIS)
        etNama = findViewById(R.id.etNama)
        spGender = findViewById(R.id.spGender)
        etAlamat = findViewById(R.id.etAlamat)
        etTgLahir = findViewById(R.id.etTglLahir)
        etFoto = findViewById(R.id.etFoto)
        btnSimpan = findViewById(R.id.btnSimpan)

        setupGenderSpinner()
        setupListeners()
    }

    private fun setupGenderSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.jenis_kelamin_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGender.adapter = adapter

        spGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedGender = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupListeners() {
        etTgLahir.setOnClickListener {
            val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                calendar.set(year, month, day)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                etTgLahir.setText(sdf.format(calendar.time))
            }
            DatePickerDialog(this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSimpan.setOnClickListener { simpanData() }
    }

    private fun simpanData() {
        val nis = etNis.text.toString().trim()
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val tgllahir = etTgLahir.text.toString().trim()
        val foto = etFoto.text.toString().trim()

        if (nis.isEmpty() || nama.isEmpty() || alamat.isEmpty() || tgllahir.isEmpty() || selectedGender.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_LONG).show()
            return
        }

        val params = mapOf(
            "nis" to nis,
            "namasiswa" to nama,
            "gender" to selectedGender,
            "alamat" to alamat,
            "tanggallahir" to tgllahir,
            "foto" to foto
        )

        requestHandler.post(Konfigurasi.URL_ADD_SISWA, params) { response, error ->
            runOnUiThread {
                if (error != null) {
                    Toast.makeText(this, "Gagal mengirim data: $error", Toast.LENGTH_LONG).show()
                } else if (response != null && response.contains("berhasil", true)) {
                    Toast.makeText(this, "Data siswa berhasil disimpan!", Toast.LENGTH_LONG).show()
                    etNis.text.clear()
                    etNama.text.clear()
                    etAlamat.text.clear()
                    etTgLahir.text.clear()
                    spGender.setSelection(0)
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menyimpan: $response", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
