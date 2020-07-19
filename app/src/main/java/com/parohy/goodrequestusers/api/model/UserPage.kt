package com.parohy.goodrequestusers.api.model

import com.google.gson.annotations.SerializedName

data class UserPage(
    @SerializedName("data") val users: List<User>,
    @SerializedName("total_pages") val pages: Int
)