package com.parohy.goodrequestusers.api.repo

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.parohy.goodrequestusers.api.ApiService
import com.parohy.goodrequestusers.api.converter.UserJsonDeserializer
import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.api.model.UserPage
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class UserDataSourceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.url("/")
//        mockWebServer.start()

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
    }

    @After
    fun destroy() {
        mockWebServer.shutdown()
    }

    @Test
    fun `when getUsers, return UserPage object`() {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(
                    "{\"page\":1,\"per_page\":5,\"total\":12,\"total_pages\":3,\"data\":[{\"id\":1,\"email\":\"george.bluth@reqres.in\",\"first_name\":\"George\",\"last_name\":\"Bluth\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg\"},{\"id\":2,\"email\":\"janet.weaver@reqres.in\",\"first_name\":\"Janet\",\"last_name\":\"Weaver\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/josephstein/128.jpg\"},{\"id\":3,\"email\":\"emma.wong@reqres.in\",\"first_name\":\"Emma\",\"last_name\":\"Wong\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/olegpogodaev/128.jpg\"},{\"id\":4,\"email\":\"eve.holt@reqres.in\",\"first_name\":\"Eve\",\"last_name\":\"Holt\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/marcoramires/128.jpg\"},{\"id\":5,\"email\":\"charles.morris@reqres.in\",\"first_name\":\"Charles\",\"last_name\":\"Morris\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/stephenmoon/128.jpg\"}],\"ad\":{\"company\":\"StatusCode Weekly\",\"url\":\"http://statuscode.org/\",\"text\":\"A weekly newsletter focusing on software development, infrastructure, the server, performance, and the stack end of things.\"}}"
                )
        )

        apiService.getUsers(1, 5)
            .test()
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
}