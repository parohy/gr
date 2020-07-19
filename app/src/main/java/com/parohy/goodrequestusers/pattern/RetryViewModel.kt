package com.parohy.goodrequestusers.pattern

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class RetryViewModel: CompositeViewModel() {
    private val retrySubject: PublishSubject<Unit> by lazy { PublishSubject.create<Unit>() }

    protected fun retryHandler(
        handler: Observable<Throwable>,
        block: ((t: Throwable) -> Unit)? = null
    ): Observable<Unit> =
        handler.flatMap {
            block?.invoke(it)
            retrySubject
        }

    fun retry() = retrySubject.onNext(Unit)
}