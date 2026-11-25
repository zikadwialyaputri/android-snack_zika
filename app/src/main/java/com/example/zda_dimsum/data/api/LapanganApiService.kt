
package com.example.zda_dimsum.data.api

import com.example.zda_dimsum.data.model.LapanganModel
import retrofit2.Response
import retrofit2.http.*

interface LapanganApiService {

    // READ
    @GET("rest/v1/lapangan")
    suspend fun getLapangan(
        @Header("apikey") apiKey: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "id.asc"
    ): List<LapanganModel>

    // CREATE
    @POST("rest/v1/lapangan")
    @Headers("Prefer: return=representation")
    suspend fun tambahLapangan(
        @Header("apikey") apiKey: String,
        @Body data: LapanganModel
    ): Response<List<LapanganModel>>

    // UPDATE
    @PATCH("rest/v1/lapangan")
    @Headers("Prefer: return=representation")
    suspend fun ubahLapangan(
        @Header("apikey") apiKey: String,
        @Query("id") id: String,
        @Body data: Map<String, @JvmSuppressWildcards Any?>
    ): Response<List<LapanganModel>>

    // DELETE
    @DELETE("rest/v1/lapangan")
    @Headers("Prefer: return=representation")
    suspend fun hapusLapangan(
        @Header("apikey") apiKey: String,
        @Query("id") id: String
    ): Response<List<LapanganModel>>
}
