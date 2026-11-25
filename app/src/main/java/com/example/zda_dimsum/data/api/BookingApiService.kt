
package com.example.zda_dimsum.data.api

import com.example.zda_dimsum.data.model.BookingModel
import retrofit2.Response
import retrofit2.http.*

interface BookingApiService {

    @GET("rest/v1/booking")
    suspend fun getBooking(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<BookingModel>

    @POST("rest/v1/booking")
    @Headers("Prefer: return=representation")
    suspend fun tambahBooking(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Body data: BookingModel
    ): Response<List<BookingModel>>

    @PATCH("rest/v1/booking")
    @Headers("Prefer: return=representation")
    suspend fun ubahBooking(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("id") id: String,
        @Body data: Map<String, @JvmSuppressWildcards Any?>
    ): Response<List<BookingModel>>

    @DELETE("rest/v1/booking")
    @Headers("Prefer: return=representation")
    suspend fun hapusBooking(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("id") id: String
    ): Response<List<BookingModel>>
}
