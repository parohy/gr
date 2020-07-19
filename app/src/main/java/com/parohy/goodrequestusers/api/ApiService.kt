package com.parohy.goodrequestusers.api

import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.api.model.UserPage
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/api/users")
    fun getUsers(@Query("page") page: Int, @Query("per_page") size: Int): Observable<UserPage>

    @GET("/api/users/{userId}")
    fun getUser(@Path("userId") uid: Int): Observable<User>
}