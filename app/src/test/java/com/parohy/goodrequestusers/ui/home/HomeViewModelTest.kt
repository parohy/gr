package com.parohy.goodrequestusers.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.parohy.goodrequestusers.api.ApiService
import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.api.model.UserPage
import com.parohy.goodrequestusers.api.repo.UserDataSource
import com.parohy.goodrequestusers.api.repo.UserRepository
import com.parohy.goodrequestusers.blockingGetFirst
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit


@RunWith(JUnit4::class)
class HomeViewModelTest {

    @Rule
    @JvmField
    var instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var testObserver: Observer<HomeViewState>

    private val apiService: ApiService = mockk(relaxUnitFun = true)
    private val userRepository: UserRepository = UserDataSource(apiService)

    @Before
    fun before() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `when vm initialized, return fetch first page`() {
        val testData = UserPage(listOf(User(1, "", "", "", "")), 1)
        every { apiService.getUsers(any(), any()) } returns Observable.just(testData)

        val vm = HomeViewModel(userRepository).apply { state.observeForever(testObserver) }

        val viewState = HomeViewState(data = testData.users)
        Assert.assertEquals(viewState, vm.state.blockingGetFirst(1, TimeUnit.SECONDS))
    }

    @Test
    fun refresh() {
    }
}