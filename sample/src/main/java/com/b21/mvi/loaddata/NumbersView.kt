package com.b21.mvi.loaddata

import io.reactivex.Observable

interface NumbersView {
    val userIntents: Observable<UserIntent>
    fun render(state: NumbersState)
}

sealed class UserIntent {
    object Refresh : UserIntent()
}
