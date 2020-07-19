package com.parohy.goodrequestusers.di

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.parohy.goodrequestusers.api.ApiService
import com.parohy.goodrequestusers.api.converter.UserJsonDeserializer
import com.parohy.goodrequestusers.api.model.User
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class ServiceModule {
    private val client: OkHttpClient by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder().addInterceptor(interceptor).build()
    }
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://reqres.in")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
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

    @Provides
    @Singleton
    fun providesApiService(): ApiService = retrofit.create(ApiService::class.java)
}