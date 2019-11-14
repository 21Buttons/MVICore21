package com.b21.mvi.sum

import com.b21.mvicore21.BaseFeature
import io.reactivex.Scheduler

class CounterFeature(
    bootstrapperAction: CounterAction? = null,
    main: Scheduler
) : BaseFeature<CounterState, CounterWish, Nothing, CounterEffect, CounterAction>(
    initialState = CounterState.Empty,
    bootstrapperAction = bootstrapperAction,
    actor = CounterActor(main),
    wishToAction = CounterWishToAction()
)

sealed class CounterState {
    data class Counter(val count: Int) : CounterState()
    object Empty : CounterState()
}

sealed class CounterWish : () -> CounterAction {
    object SumClick : CounterWish() {
        override fun invoke() = CounterAction.SumCounter
    }

    object SubtractClick : CounterWish() {
        override fun invoke() = CounterAction.SubtractCounter
    }
}

sealed class CounterAction {
    object SumCounter : CounterAction()
    object SubtractCounter : CounterAction()
}

sealed class CounterEffect : (CounterState) -> CounterState {
    data class DataReceived(val increment: Int) : CounterEffect() {
        override fun invoke(previousState: CounterState): CounterState {
            return when (previousState) {
                is CounterState.Empty -> CounterState.Counter(increment)
                is CounterState.Counter -> CounterState.Counter(previousState.count + increment)
            }
        }
    }
}
