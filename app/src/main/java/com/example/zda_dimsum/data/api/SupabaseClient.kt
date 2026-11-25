
package com.example.zda_dimsum.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {

    const val BASE_URL =
        "https://nbpajtwhzoyrsivricfz.supabase.co/"

    const val API_KEY =
        "sb_publishable_oGB2_412-P4KJXoTe-VFag_X_W_fYLm"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val lapanganService: LapanganApiService by lazy {
        retrofit.create(LapanganApiService::class.java)
    }

    val bookingService: BookingApiService by lazy {
        retrofit.create(BookingApiService::class.java)
    }
}
