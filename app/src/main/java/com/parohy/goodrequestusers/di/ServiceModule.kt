package com.parohy.goodrequestusers.di

import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.parohy.goodrequestusers.api.ApiService
import com.parohy.goodrequestusers.api.converter.UserJsonDeserializer
import com.parohy.goodrequestusers.api.model.User
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class ServiceModule(private val context: Context) {
    private val client: OkHttpClient by lazy {
        val cache = Cache(context.cacheDir, (5 * 1024 * 1024).toLong())

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .cache(cache)
            .addInterceptor { chain ->
                val request = if (isConnected() == true)
                    chain.request().newBuilder().header("Cache-Control", "public, max-age=" + 5)
                        .build()
                else
                    chain.request().newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                    ).build()
                chain.proceed(request)
            }
            .build()
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

    private fun isConnected(): Boolean? {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetwork != null
    }

    @Provides
    @Singleton
    fun providesApiService(): ApiService = retrofit.create(ApiService::class.java)
}