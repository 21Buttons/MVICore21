package com.b21.mvi.sum

import io.reactivex.rxjava3.core.Observable


interface CounterView {
    val userIntents: Observable<UserIntent>
    fun render(state: CounterState)
}

sealed class UserIntent {
    object Sum : UserIntent()
    object Subtract : UserIntent()
}
