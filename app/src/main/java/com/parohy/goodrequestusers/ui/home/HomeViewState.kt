package com.parohy.goodrequestusers.ui.home

import com.parohy.goodrequestusers.api.model.User

data class HomeViewState(
    val loading: Boolean = false,
    val data: List<User> = listOf(),
    val refresh: Boolean = false,
    val error: Throwable? = null,
    val silentError: Throwable? = null
)

fun HomeViewState?.copyData(
    value: List<User> = listOf(),
    silentError: Throwable? = null
): HomeViewState =
    this?.copy(
        loading = false,
        error = null,
        refresh = false,
        silentError = silentError,
        data = listOf(*data.toTypedArray(), *value.toTypedArray())
    )
        ?: HomeViewState(data = value)