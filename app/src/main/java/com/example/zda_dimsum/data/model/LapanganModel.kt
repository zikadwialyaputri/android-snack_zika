
package com.example.zda_dimsum.data.model

import com.google.gson.annotations.SerializedName

data class LapanganModel(

    val id: Long? = null,

    val nama: String,

    val lokasi: String,

    val harga: Double,

    val deskripsi: String? = null,

    val gambar: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)
