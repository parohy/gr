package com.parohy.goodrequestusers.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.parohy.goodrequestusers.api.repo.UserRepository
import com.parohy.goodrequestusers.pattern.CompositeViewModel
import com.parohy.goodrequestusers.pattern.RetryViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DetailViewModel(private val userRepository: UserRepository): RetryViewModel() {
    private val _state: MutableLiveData<DetailViewState> = MutableLiveData()
    val state: LiveData<DetailViewState>
        get() = _state

    init {
        _state.value = DetailViewState(loading = true)
    }

    fun loadUser(uid: Int) {
        if (isUidValid(uid))
            userRepository.getUser(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::initialStateOnSubscribe)
                .retryWhen(this::retryHandler)
                .subscribe({
                    _state.value = DetailViewState(data = it)
                }, {
                    _state.value = DetailViewState(error = it)
                })
                .toDisposables()
    }

    private fun retryHandler(handler: Observable<Throwable>): Observable<Unit> =
        retryHandler(handler) {
            _state.value = if (_state.value?.data == null)
                DetailViewState(error = it)
            else
                DetailViewState(silentError = it)
        }

    private fun isUidValid(uid: Int): Boolean {
        return if (uid == -1) {
            _state.value =
                DetailViewState(error = IllegalArgumentException("Arg uid cannot be $uid"))
            false
        } else true
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