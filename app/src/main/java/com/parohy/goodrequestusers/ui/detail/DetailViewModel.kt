package com.parohy.goodrequestusers.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.parohy.goodrequestusers.api.repo.UserRepository
import com.parohy.goodrequestusers.pattern.CompositeViewModel
import com.parohy.goodrequestusers.pattern.RetryViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import kotlin.IllegalArgumentException

class DetailViewModel(private val userRepository: UserRepository): RetryViewModel() {
    private val _state: MutableLiveData<DetailViewState> = MutableLiveData()
    val state: LiveData<DetailViewState>
        get() = _state
    val loadUserSubject: PublishSubject<LoadUserEvent> = PublishSubject.create()

    init {
        loadUserSubject
            .doOnNext(this::isUidValid)
            .flatMapSingle { userRepository.getUser(it.uid) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(this::initialStateOnSubscribe)
            .subscribe({
                _state.value = DetailViewState(data = it)
            }, {
                _state.value = DetailViewState(error = it)
            })
            .toDisposables()
    }

    private fun isUidValid(event: LoadUserEvent) {
        if (event.uid == -1) throw IllegalArgumentException("Arg uid cannot be ${event.uid}")
    }

    private fun initialStateOnSubscribe(d: Disposable) {
        _state.value = DetailViewState(loading = true)
    }

    class Factory @Inject constructor(
        private val userRepository: UserRepository
    ): CompositeViewModel.Factory<DetailViewModel>() {
        override fun create(handle: SavedStateHandle): DetailViewModel {
            return DetailViewModel(userRepository)
        }
    }

    data class LoadUserEvent(val uid: Int)
}