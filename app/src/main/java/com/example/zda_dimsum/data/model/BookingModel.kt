
package com.example.zda_dimsum.data.model

import com.google.gson.annotations.SerializedName

data class BookingModel(

    val id: Long? = null,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("lapangan_id")
    val lapanganId: Long,

    val tanggal: String,

    val jam: String,

    @SerializedName("total_harga")
    val totalHarga: Double,

    val status: String = "Menunggu",

    @SerializedName("created_at")
    val createdAt: String? = null
)
