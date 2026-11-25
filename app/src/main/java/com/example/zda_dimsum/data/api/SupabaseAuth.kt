
package com.example.zda_dimsum.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object SupabaseAuth {

    suspend fun register(
        nama: String,
        email: String,
        username: String,
        tanggalLahir: String,
        jenisKelamin: String,
        password: String
    ): JSONObject = withContext(Dispatchers.IO) {

        val response = SupabaseClient.authService.register(
            apiKey = SupabaseClient.API_KEY,
            request = RegisterUserRequest(
                nama = nama,
                email = email,
                username = username,
                password = password,
                tanggalLahir = tanggalLahir,
                jenisKelamin = jenisKelamin
            )
        )

        if (!response.isSuccessful) {
            val error = response.errorBody()?.string()
            throw Exception(
                error ?: "Registrasi gagal (${response.code()})"
            )
        }

        val result = response.body()
            ?: throw Exception("Respons registrasi kosong")

        JSONObject(result.toString())
    }

    suspend fun login(
        email: String,
        password: String
    ): JSONObject = withContext(Dispatchers.IO) {

        val response = SupabaseClient.authService.login(
            apiKey = SupabaseClient.API_KEY,
            request = LoginUserRequest(
                email = email,
                password = password
            )
        )

        if (!response.isSuccessful) {
            val error = response.errorBody()?.string()
            throw Exception(
                error ?: "Login gagal (${response.code()})"
            )
        }

        val result = response.body()
            ?: throw Exception("Respons login kosong")

        JSONObject(result.toString())
    }
}
