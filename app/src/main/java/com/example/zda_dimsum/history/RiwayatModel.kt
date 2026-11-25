
package com.example.zda_dimsum.history

data class RiwayatModel(
    val id: Long,
    val namaLapangan: String,
    val lokasi: String,
    val tanggal: String,
    val jam: String,
    val harga: String,
    val status: String,
    val tanggalDatabase: String,
    val jamDatabase: String
)
