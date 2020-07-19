package com.parohy.goodrequestusers

import androidx.lifecycle.LiveData
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.blockingGetFirst(timeout: Long = 0, unit: TimeUnit = TimeUnit.SECONDS): T {
    val latch = CountDownLatch(1)
    var value: T? = null
    this.observeForever {
        value = it
        latch.countDown()
    }
    await(latch, timeout, unit)
    return value!!
}

fun <T> LiveData<T>.blockingGetN(count: Int, timeout: Long = 0, unit: TimeUnit = TimeUnit.SECONDS): List<T> {
    val latch = CountDownLatch(count)
    val values = mutableListOf<T>()
    this.observeForever {
        values.add(it)
        latch.countDown()
    }
    await(latch, timeout, unit)
    return values
}

private fun await(latch: CountDownLatch, timeout: Long, unit: TimeUnit) {
    if (timeout > 0)
        latch.await(timeout, unit)
    else
        latch.await()
}