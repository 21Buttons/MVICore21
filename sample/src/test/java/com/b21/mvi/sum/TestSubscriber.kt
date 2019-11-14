package com.b21.mvi.sum

import io.reactivex.subscribers.TestSubscriber

fun <T> TestSubscriber<T>.assertValuesAndClear(vararg values: T): TestSubscriber<T> {
    return this.apply {
        assertValues(*values)
            .values()
            .clear()
    }
}
