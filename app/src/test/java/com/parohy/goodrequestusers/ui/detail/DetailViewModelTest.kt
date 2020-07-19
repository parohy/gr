package com.parohy.goodrequestusers.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.parohy.goodrequestusers.api.ApiService
import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.api.repo.UserDataSource
import com.parohy.goodrequestusers.api.repo.UserRepository
import com.parohy.goodrequestusers.blockingGetFirst
import com.parohy.goodrequestusers.blockingGetN
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers
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
class DetailViewModelTest {
    @Rule
    @JvmField
    var instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var testObserver: Observer<DetailViewState>

    private val apiService: ApiService = mockk(relaxUnitFun = true)
    private val userRepository: UserRepository = UserDataSource(apiService)

    @Before
    fun before() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        MockitoAnnotations.initMocks(this)
    }

    private fun delayedRepoResponse(toReturn: Observable<User>) {
        every { apiService.getUser(any()) } returns toReturn
            .delay(500, TimeUnit.MILLISECONDS)
    }

    @Test
    fun `when vm initialized, state should start with loading`() {
        delayedRepoResponse(Observable.just(User(1, "Joe", "Ananas", "email@email.com", "")))

        val vm = DetailViewModel(userRepository).apply { state.observeForever(testObserver) }

        val viewState = DetailViewState(loading = true)
        Assert.assertEquals(viewState, vm.state.blockingGetFirst(1, TimeUnit.SECONDS))

    }

    @Test
    fun `when getUser, state should contain data`() {
        delayedRepoResponse(Observable.just(User(1, "Joe", "Ananas", "email@email.com", "")))

        val vm = DetailViewModel(userRepository).apply { state.observeForever(testObserver) }
        vm.loadUser(1)

        val viewState = DetailViewState(data = User(1, "Joe", "Ananas", "email@email.com", ""))
        Assert.assertEquals(viewState, vm.state.blockingGetN(2, 1, TimeUnit.SECONDS).last())
    }

    @Test
    fun `when getUser, called with -1, state error should contain IllegalArgumentException`() {
        val vm = DetailViewModel(userRepository).apply { state.observeForever(testObserver) }
        vm.loadUser(-1)

        Assert.assertThat(
            vm.state.blockingGetFirst(1, TimeUnit.SECONDS).error,
            CoreMatchers.instanceOf(IllegalArgumentException::class.java)
        )
    }
}