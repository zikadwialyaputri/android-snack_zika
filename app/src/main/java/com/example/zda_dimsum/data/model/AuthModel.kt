package com.example.zda_dimsum.data.model

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    val email: String,
    val password: String,
    val data: Map<String, String>
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,

    @SerializedName("token_type")
    val tokenType: String? = null,

    @SerializedName("expires_in")
    val expiresIn: Long? = null,

    @SerializedName("refresh_token")
    val refreshToken: String? = null,

    val user: AuthUser? = null
)

data class AuthUser(
    val id: String? = null,
    val email: String? = null
)
