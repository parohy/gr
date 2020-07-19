package com.parohy.goodrequestusers

import android.app.Application
import com.parohy.goodrequestusers.di.AppComponent
import com.parohy.goodrequestusers.di.DaggerAppComponent
import com.parohy.goodrequestusers.di.ServiceModule

class App: Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .serviceModule(ServiceModule(this))
            .build()
    }
}

fun Application.diComponent(): AppComponent = (this as App).appComponent