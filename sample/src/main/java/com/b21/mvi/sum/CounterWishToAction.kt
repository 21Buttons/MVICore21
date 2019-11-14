package com.b21.mvi.sum

import com.b21.mvicore21.WishToAction

class CounterWishToAction : WishToAction<CounterWish, CounterState, CounterAction> {

    override fun invoke(wish: CounterWish, state: CounterState): CounterAction? {
        return when (wish) {
            CounterWish.SumClick -> CounterAction.SumCounter
            CounterWish.SubtractClick -> CounterAction.SubtractCounter
        }
    }
}
