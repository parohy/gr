package com.parohy.goodrequestusers.api.repo

import android.util.Log
import com.parohy.goodrequestusers.api.ApiService
import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.api.model.UserPage
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val apiService: ApiService
): UserRepository {
    override fun getPage(page: Int, size: Int): Observable<UserPage> =
        apiService.getUsers(page, size)
            .onErrorResumeNext { t: Throwable ->
                if (t is UnknownHostException)
                    Observable.error<UserPage>(RuntimeException("No internet connection"))
                else
                    Observable.error(t)
            }
            .subscribeOn(Schedulers.io())

    override fun getUser(uid: Int): Single<User> =
        apiService.getUser(uid)
            .singleOrError()
            .onErrorResumeNext { t: Throwable ->
                if (t is UnknownHostException)
                    Single.error(RuntimeException("No internet connection"))
                else
                    Single.error(t)
            }
            .subscribeOn(Schedulers.io())
}