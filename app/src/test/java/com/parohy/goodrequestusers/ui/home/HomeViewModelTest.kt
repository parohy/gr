package com.parohy.goodrequestusers.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.parohy.goodrequestusers.api.ApiService
import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.api.model.UserPage
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
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.net.UnknownHostException
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

    private fun delayedRepoResponse(toReturn: Observable<UserPage>) {
        every { apiService.getUsers(any(), any()) } returns toReturn
            .delay(500, TimeUnit.MILLISECONDS)
    }

    @Test
    fun `ViewModel is expected to start with loading state`() {
        val testData = UserPage(listOf(User(1, "", "", "", "")), 1)
        delayedRepoResponse(Observable.just(testData))

        val vm = HomeViewModel(userRepository).apply { state.observeForever(testObserver) }

        val viewState = HomeViewState(loading = true)
        Assert.assertEquals(viewState, vm.state.blockingGetFirst(2, TimeUnit.SECONDS))
    }

    @Test
    fun `when vm initialized, return fetch first page`() {
        val testData = UserPage(listOf(User(1, "", "", "", "")), 1)
        delayedRepoResponse(Observable.just(testData))

        val vm = HomeViewModel(userRepository).apply { state.observeForever(testObserver) }

        val viewState = HomeViewState(data = testData.users)
        Assert.assertEquals(viewState, vm.state.blockingGetN(2, 1, TimeUnit.SECONDS).last())
    }

    @Test
    @Ignore
    fun `when load next page, should concat to previous result`() {
        val firstPage = UserPage(listOf(User(1, "", "", "", "")), 2)
        delayedRepoResponse(Observable.just(firstPage))

        val vm = HomeViewModel(userRepository).apply { state.observeForever(testObserver) }
        val secondPage = UserPage(listOf(User(2, "", "", "", "")), 2)
        delayedRepoResponse(Observable.just(secondPage))

        vm.loadPage()

        val viewState = HomeViewState(
            data = listOf(
                *firstPage.users.toTypedArray(),
                *secondPage.users.toTypedArray()
            )
        )
        Assert.assertEquals(viewState, vm.state.blockingGetN(2, 2, TimeUnit.SECONDS).last())
    }

    @Test
    fun `when failed to connect after init, state error should contain RuntimeExceptionError`() {
        delayedRepoResponse(Observable.error(UnknownHostException()))

        val vm = HomeViewModel(userRepository).apply { state.observeForever(testObserver) }

        Assert.assertThat(
            vm.state.blockingGetFirst(1, TimeUnit.SECONDS).error,
            CoreMatchers.instanceOf(RuntimeException::class.java)
        )
    }

    @Test
    fun `when failed for other reason after init, state error should contain that error`() {
        delayedRepoResponse(Observable.error(IllegalArgumentException()))

        val vm = HomeViewModel(userRepository).apply { state.observeForever(testObserver) }

        Assert.assertThat(
            vm.state.blockingGetN(2, 1, TimeUnit.SECONDS).last().error,
            CoreMatchers.instanceOf(IllegalArgumentException::class.java)
        )
    }

    @Test
    fun `refresh event should fetch and return pages previously fetched`() {
        val firstPage = UserPage(listOf(User(1, "", "", "", "")), 2)
        delayedRepoResponse(Observable.just(firstPage))

        val vm = HomeViewModel(userRepository).apply { state.observeForever(testObserver) }

        Assert.assertEquals(
            HomeViewState(data = firstPage.users, refresh = false),
            vm.state.blockingGetN(2, 10, TimeUnit.SECONDS).last()
        )

        vm.refresh()
        Assert.assertEquals(
            HomeViewState(data = firstPage.users, refresh = true),
            vm.state.blockingGetN(2, 10, TimeUnit.SECONDS).last()
        )
    }

    @Test
    fun `when refresh failed to connect, state should contain silentError RuntimeException`() {
        val firstPage = UserPage(listOf(User(1, "", "", "", "")), 2)
        delayedRepoResponse(Observable.just(firstPage))

        val vm = HomeViewModel(userRepository).apply { state.observeForever(testObserver) }

        Assert.assertEquals(
            HomeViewState(data = firstPage.users, refresh = false),
            vm.state.blockingGetN(2, 10, TimeUnit.SECONDS).last()
        )

        delayedRepoResponse(Observable.error(UnknownHostException()))
        vm.refresh()
        val actualState = vm.state.blockingGetN(2, 10, TimeUnit.SECONDS).last()
        Assert.assertThat(
            actualState.silentError,
            CoreMatchers.instanceOf(RuntimeException::class.java)
        )
        Assert.assertNull(actualState.error)
    }

    @Test
    fun `when refresh failed for other reason, state should contain silentError RuntimeException`() {
        val firstPage = UserPage(listOf(User(1, "", "", "", "")), 2)
        delayedRepoResponse(Observable.just(firstPage))

        val vm = HomeViewModel(userRepository).apply { state.observeForever(testObserver) }

        Assert.assertEquals(
            HomeViewState(data = firstPage.users, refresh = false),
            vm.state.blockingGetN(2, 10, TimeUnit.SECONDS).last()
        )

        delayedRepoResponse(Observable.error(IndexOutOfBoundsException()))
        vm.refresh()
        val actualState = vm.state.blockingGetN(2, 10, TimeUnit.SECONDS).last()
        Assert.assertThat(
            actualState.silentError,
            CoreMatchers.instanceOf(IndexOutOfBoundsException::class.java)
        )
        Assert.assertNull(actualState.error)
    }
}