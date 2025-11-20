package com.example.tenizenapp

object Konfigurasi {
    // Ganti dengan IP server Anda
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Auth Endpoints
    const val URL_LOGIN = BASE_URL + "login.php"
    const val URL_REGISTER = BASE_URL + "register.php"
    const val URL_VERIFY_OTP = BASE_URL + "verify_otp.php"
    const val URL_RESEND_OTP = BASE_URL + "resend_otp.php"

    // Siswa Endpoints
    const val URL_GET_ALL_SISWA = BASE_URL + "tampil_siswa.php"
    const val URL_GET_SISWA_BY_NIS = BASE_URL + "get_siswa_nis.php"
    const val URL_ADD_SISWA = BASE_URL + "tambah_siswa.php"
    const val URL_UPDATE_SISWA = BASE_URL + "update_siswa.php"
    const val URL_DELETE_SISWA = BASE_URL + "hapus_siswa.php"

    // Produk Endpoints
    const val URL_GET_ALL_PRODUK = BASE_URL + "tampil_produk.php"
    const val URL_ADD_PRODUK = BASE_URL + "tambah_produk.php"
    const val URL_DELETE_PRODUK = BASE_URL + "hapus_produk.php"
    const val URL_UPDATE_PRODUK = BASE_URL + "update_produk.php"

    // Transaksi Endpoints
    const val URL_GET_ALL_TRANSAKSI = BASE_URL + "tampil_transaksi.php"
    const val URL_ADD_TRANSAKSI = BASE_URL + "tambah_transaksi.php"

    // Parameter Keys
    const val KEY_EMAIL = "email"
    const val KEY_PASSWORD = "password"
    const val KEY_USERNAME = "username"
    const val KEY_OTP = "otp"

    const val KEY_NIS = "nis"
    const val KEY_NAME = "name"
    const val KEY_GENDER = "gender"
    const val KEY_ALAMAT = "alamat"
    const val KEY_TANGGAL_LAHIR = "tanggallahir"
    const val KEY_FOTO = "foto"

    const val KEY_ID_PRODUK = "idproduk"
    const val KEY_NAMA_PRODUK = "namaproduk"
    const val KEY_JUMLAH = "jumlah"
    const val KEY_HARGA = "harga"
    const val KEY_BARCODE = "barcode"

    const val KEY_JUMLAH_BELI = "jumlahbeli"
    const val KEY_TOTAL = "total"
    const val KEY_BAYAR = "bayar"
    const val KEY_KEMBALIAN = "kembalian"

    // JSON Tags
    const val TAG_JSON_ARRAY = "result"
    const val TAG_STATUS = "status"
    const val TAG_MESSAGE = "message"
    const val TAG_DATA = "data"
    const val TAG_OTP = "otp"
}