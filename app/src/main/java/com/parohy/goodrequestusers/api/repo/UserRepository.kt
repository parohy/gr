package com.parohy.goodrequestusers.api.repo

import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.api.model.UserPage
import io.reactivex.Observable

interface UserRepository {
    fun getPage(page: Int = 1, size: Int = 5): Observable<UserPage>
    fun getUser(uid: Int): Observable<User>
}