package com.parohy.goodrequestusers.pattern

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class CompositeViewModel: ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    abstract class Factory<VM: CompositeViewModel>:
        ViewModelFactory<VM>

    override fun onCleared() {
        compositeDisposable.clear()
    }

    protected fun addDisposable(vararg d: Disposable) {
        compositeDisposable.addAll(*d)
    }

    fun Disposable.toDisposables() {
        compositeDisposable.addAll(this)
    }
}