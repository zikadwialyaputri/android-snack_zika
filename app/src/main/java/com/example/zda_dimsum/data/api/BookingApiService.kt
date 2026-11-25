
package com.example.zda_dimsum.data.api

import com.example.zda_dimsum.data.model.BookingModel
import retrofit2.Response
import retrofit2.http.*

interface BookingApiService {

    @GET("rest/v1/booking")
    suspend fun getBooking(
        @Header("apikey") apiKey: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<BookingModel>

    @POST("rest/v1/booking")
    @Headers("Prefer: return=representation")
    suspend fun tambahBooking(
        @Header("apikey") apiKey: String,
        @Body data: BookingModel
    ): Response<List<BookingModel>>

    // UPDATE booking: filter berdasarkan ID dan pemilik booking
    @PATCH("rest/v1/booking")
    @Headers("Prefer: return=representation")
    suspend fun ubahBooking(
        @Header("apikey") apiKey: String,
        @Query("id") id: String,
        @Query("user_id") userId: String,
        @Body data: Map<String, @JvmSuppressWildcards Any?>
    ): Response<List<BookingModel>>

    // DELETE booking: filter berdasarkan ID dan pemilik booking
    @DELETE("rest/v1/booking")
    @Headers("Prefer: return=representation")
    suspend fun hapusBooking(
        @Header("apikey") apiKey: String,
        @Query("id") id: String,
        @Query("user_id") userId: String
    ): Response<List<BookingModel>>
}
