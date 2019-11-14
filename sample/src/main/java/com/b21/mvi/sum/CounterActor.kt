package com.b21.mvi.sum

import com.b21.mvicore21.Actor
import io.reactivex.Flowable
import io.reactivex.Scheduler

class CounterActor(
    private val main: Scheduler
) : Actor<CounterAction, CounterEffect> {

    override fun invoke(action: CounterAction): Flowable<CounterEffect> {
        return when (action) {
            is CounterAction.SumCounter -> {
                Flowable.just<CounterEffect>(CounterEffect.DataReceived(1))
            }
            is CounterAction.SubtractCounter -> {
                Flowable.just<CounterEffect>(CounterEffect.DataReceived(-1))
            }
        }
            .observeOn(main)
    }
}
