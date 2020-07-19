package com.parohy.goodrequestusers.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.parohy.goodrequestusers.api.repo.UserRepository
import com.parohy.goodrequestusers.pattern.RetryViewModel
import com.parohy.goodrequestusers.pattern.CompositeViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeViewModel(private val userRepository: UserRepository): RetryViewModel() {
    private val _state: MutableLiveData<HomeViewState> = MutableLiveData()
    val state: LiveData<HomeViewState>
        get() = _state

    private var totalPages: Int = 0
    private var currentPage: Int = 1
    private var previousPage: Int = 1
    private val loadPageSubject: PublishSubject<Unit> = PublishSubject.create()

    init {
        userRepository.getPage(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterNext { totalPages = it.pages }
            .doOnSubscribe(this::initialStateOnSubscribe)
            .retryWhen(this::retryHandler)
            .subscribe({
                _state.value = HomeViewState(data = it.users)
            }, {
                _state.value = HomeViewState(error = it)
            })
            .toDisposables()

        loadPageSubject
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .map { LoadDataEvent(++currentPage) }
            .filter(this::isNotPreviousPage)
            .filter(this::isMaxPage)
            .doAfterNext { previousPage = it.page }
            .flatMap { userRepository.getPage(it.page) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retryWhen(this::retrySilentHandler)
            .subscribe({
                _state.value = _state.value.copyData(it.users)
            }, {
                _state.value = HomeViewState(silentError = it)
            }).toDisposables()
    }

    private fun initialStateOnSubscribe(d: Disposable) {
        _state.value = HomeViewState(loading = true)
    }

    private fun retryHandler(handler: Observable<Throwable>): Observable<Unit> =
        retryHandler(handler) { _state.value = HomeViewState(error = it) }

    private fun retrySilentHandler(handler: Observable<Throwable>): Observable<Unit> =
        retryHandler(handler) { _state.value = _state.value.copyData(silentError = it) }

    private fun isNotPreviousPage(event: LoadDataEvent): Boolean =
        event.page != previousPage

    private fun isMaxPage(event: LoadDataEvent): Boolean =
        (event.page <= totalPages)
            .also {
                if (!it) currentPage = totalPages
            }

    fun loadPage() = loadPageSubject.onNext(Unit)

    fun refresh() {
        userRepository.getPage(1, currentPage * 5)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retryWhen(this::retrySilentHandler)
            .subscribe({
                _state.value = HomeViewState(refresh = true, data = it.users)
            }, {
                _state.value = HomeViewState(silentError = it)
            }).toDisposables()
    }

    class Factory @Inject constructor(
        private val userRepository: UserRepository
    ): CompositeViewModel.Factory<HomeViewModel>() {
        override fun create(handle: SavedStateHandle): HomeViewModel {
            return HomeViewModel(userRepository)
        }
    }

    private data class LoadDataEvent(val page: Int)
}