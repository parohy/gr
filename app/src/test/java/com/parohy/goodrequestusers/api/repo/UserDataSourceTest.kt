package com.parohy.goodrequestusers.api.repo

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.parohy.goodrequestusers.api.ApiService
import com.parohy.goodrequestusers.api.converter.UserJsonDeserializer
import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.api.model.UserPage
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class UserDataSourceTest {
    private lateinit var apiService: ApiService
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        apiService = Retrofit.Builder()
            .baseUrl("https://reqres.in")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(
                            User::class.java,
                            UserJsonDeserializer()
                        )
                        .create()
                )
            )
            .build()
            .create(ApiService::class.java)

        userRepository = UserDataSource(apiService)
    }

    @Test
    fun `when getUsers, return UserPage object`() {
        userRepository.getPage()
            .test()
            .await()
            .assertNoErrors()
            .assertValue(
                UserPage(
                    users = listOf(
                        User(
                            id = 1,
                            name = "George",
                            surname = "Bluth",
                            email = "george.bluth@reqres.in",
                            avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg"
                        ),
                        User(
                            id = 2,
                            name = "Janet",
                            surname = "Weaver",
                            email = "janet.weaver@reqres.in",
                            avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/josephstein/128.jpg"
                        ),
                        User(
                            id = 3,
                            name = "Emma",
                            surname = "Wong",
                            email = "emma.wong@reqres.in",
                            avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/olegpogodaev/128.jpg"
                        ),
                        User(
                            id = 4,
                            name = "Eve",
                            surname = "Holt",
                            email = "eve.holt@reqres.in",
                            avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/marcoramires/128.jpg"
                        ),
                        User(
                            id = 5,
                            name = "Charles",
                            surname = "Morris",
                            email = "charles.morris@reqres.in",
                            avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/stephenmoon/128.jpg"
                        )
                    ), pages = 3
                )
            )
    }

    @Test
    fun `when getPage, requested page has no data, should return UserPage with empty data`() {
        userRepository.getPage(10)
            .test()
            .await()
            .assertValue(UserPage(listOf(), 3))
    }

    @Test
    fun `when getPage failed to connect, return RuntimeException`() {
        apiService = Retrofit.Builder()
            .baseUrl("https://r.in")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(
                            User::class.java,
                            UserJsonDeserializer()
                        )
                        .create()
                )
            )
            .build()
            .create(ApiService::class.java)

        userRepository = UserDataSource(apiService)

        userRepository.getPage()
            .test()
            .await()
            .assertError(RuntimeException::class.java)
    }

    @Test
    fun `when getUser, return parsed User`() {
        userRepository.getUser(1)
            .test()
            .await()
            .assertValue(
                User(
                    id = 1,
                    name = "George",
                    surname = "Bluth",
                    email = "george.bluth@reqres.in",
                    avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg"
                )
            )
    }

    @Test
    fun `when getUser, when not exists return HttpException`() {
        userRepository.getUser(99)
            .test()
            .await()
            .assertError(HttpException::class.java)

    }
}