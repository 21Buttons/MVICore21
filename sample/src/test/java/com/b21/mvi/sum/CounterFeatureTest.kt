package com.b21.mvi.sum

import io.reactivex.schedulers.Schedulers
import org.junit.Test

class CounterFeatureTest {

    private fun createFeature(bootstapperAction: CounterAction? = null) =
        CounterFeature(bootstapperAction, Schedulers.trampoline())

    @Test
    fun runOnlyWithInitialState() {
        val feature = createFeature()

        feature.test().assertValue(CounterState.Empty)
    }

    @Test
    fun runWithInitialStateAndBootstrapper() {
        val feature = createFeature(CounterAction.SumCounter)

        val testSubscriber = feature.test()

        testSubscriber
            .assertValues(CounterState.Empty, CounterState.Counter(1))
    }

    @Test
    fun runWithActorEmittingSum() {
        val feature = createFeature()

        val testSubscriber = feature.test()

        testSubscriber
            .assertValuesAndClear(CounterState.Empty)

        feature.accept(CounterWish.SumClick)

        testSubscriber.assertValue(CounterState.Counter(1))
    }

    @Test
    fun runWithActorEmittingSubtract() {
        val feature = createFeature()

        val testSubscriber = feature.test()

        testSubscriber
            .assertValuesAndClear(CounterState.Empty)

        feature.accept(CounterWish.SubtractClick)

        testSubscriber.assertValue(CounterState.Counter(-1))
    }

    @Test
    fun runWithActorEmittingMultipleEvents() {
        val feature = createFeature()

        val testSubscriber = feature.test()

        testSubscriber
            .assertValuesAndClear(CounterState.Empty)

        repeat(5) { feature.accept(CounterWish.SumClick) }

        testSubscriber.assertValues(
            CounterState.Counter(1),
            CounterState.Counter(2),
            CounterState.Counter(3),
            CounterState.Counter(4),
            CounterState.Counter(5)
        )
    }
}
