package com.parohy.goodrequestusers.api.repo

import com.parohy.goodrequestusers.api.ApiService
import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.api.model.UserPage
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val apiService: ApiService
): UserRepository {
    override fun getPage(page: Int, size: Int): Observable<UserPage> =
        apiService.getUsers(page, size)
            .timeout(10, TimeUnit.SECONDS)
            .onErrorResumeNext { t: Throwable ->
                if (t is UnknownHostException)
                    Observable.error<UserPage>(RuntimeException("No internet connection"))
                else
                    Observable.error(t)
            }
            .subscribeOn(Schedulers.io())

    override fun getUser(uid: Int): Observable<User> =
        apiService.getUser(uid)
            .timeout(10, TimeUnit.SECONDS)
            .onErrorResumeNext { t: Throwable ->
                if (t is UnknownHostException)
                    Observable.error(RuntimeException("No internet connection"))
                else
                    Observable.error(t)
            }
            .subscribeOn(Schedulers.io())
}