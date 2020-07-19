package com.parohy.goodrequestusers.api.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    @SerializedName("first_name") val name: String,
    @SerializedName("last_name") val surname: String,
    val email: String,
    val avatar: String
)