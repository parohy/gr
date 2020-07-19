package com.parohy.goodrequestusers.ui.detail

import com.parohy.goodrequestusers.api.model.User

data class DetailViewState(
    val loading: Boolean = false,
    val data: User? = null,
    val error: Throwable? = null,
    val silentError: Throwable? = null
)