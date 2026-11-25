
package com.example.zda_dimsum.data.api

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class RegisterUserRequest(
    @SerializedName("p_nama")
    val nama: String,

    @SerializedName("p_email")
    val email: String,

    @SerializedName("p_username")
    val username: String,

    @SerializedName("p_password")
    val password: String,

    @SerializedName("p_tanggal_lahir")
    val tanggalLahir: String,

    @SerializedName("p_jenis_kelamin")
    val jenisKelamin: String
)

data class LoginUserRequest(
    @SerializedName("p_email")
    val email: String,

    @SerializedName("p_password")
    val password: String
)

interface AuthApiService {

    @POST("rest/v1/rpc/register_user")
    suspend fun register(
        @Header("apikey") apiKey: String,
        @Body request: RegisterUserRequest
    ): Response<JsonObject>

    @POST("rest/v1/rpc/login_user")
    suspend fun login(
        @Header("apikey") apiKey: String,
        @Body request: LoginUserRequest
    ): Response<JsonObject>
}
