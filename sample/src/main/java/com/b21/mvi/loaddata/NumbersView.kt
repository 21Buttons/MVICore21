package com.b21.mvi.loaddata

import io.reactivex.Observable

interface NumbersView {
    val userIntents: Observable<UserIntent>
    fun render(state: NumbersState)
    fun showSelectedItem(item: String)
}

sealed class UserIntent {
    data class SelectedItem(val item: String) : UserIntent()
    object Refresh : UserIntent()
}
