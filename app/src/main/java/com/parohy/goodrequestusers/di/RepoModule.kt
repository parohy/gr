package com.parohy.goodrequestusers.di

import com.parohy.goodrequestusers.api.ApiService
import com.parohy.goodrequestusers.api.repo.UserDataSource
import com.parohy.goodrequestusers.api.repo.UserRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoModule {
    @Provides
    @Singleton
    fun providesUserRepository(apiService: ApiService): UserRepository = UserDataSource(apiService)
}