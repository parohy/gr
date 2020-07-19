package com.parohy.goodrequestusers.di

import com.parohy.goodrequestusers.ui.detail.DetailFragment
import com.parohy.goodrequestusers.ui.home.HomeFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ServiceModule::class, RepoModule::class])
interface AppComponent {
    fun injectHomeFragment(homeFragment: HomeFragment)
    fun injectDetailFragment(detailFragment: DetailFragment)
}