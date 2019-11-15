package com.b21.mvi.sum

import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.reactivestreams.Subscriber

class CounterPresenterTest {
    private val userIntentsRelay: Relay<UserIntent> = PublishRelay.create()
    private val view: CounterView = mock {
        on { userIntents } doReturn userIntentsRelay
    }
    private val feature: CounterFeature = mock()
    private val owner: LifecycleOwner = mock()
    private lateinit var stateStream: Subscriber<CounterState>

    private val presenter = CounterPresenter(view, feature)

    @After
    fun tearDown() {
        verifyNoMoreInteractions(view, feature, owner)
    }

    @Before
    fun launchPresenter() {
        presenter.onCreate(owner)

        argumentCaptor<Subscriber<CounterState>>().apply {
            verify(feature).subscribeActual(capture())
            stateStream = firstValue
        }

        verify(view).userIntents
    }

    @Test
    fun testSum() {
        userIntentsRelay.accept(UserIntent.Sum)

        verify(feature).accept(CounterWish.SumClick)
    }

    @Test
    fun testSubtract() {
        userIntentsRelay.accept(UserIntent.Subtract)

        verify(feature).accept(CounterWish.SubtractClick)
    }

    @Test
    fun testRender() {
        stateStream.onNext(CounterState.Counter(1))

        verify(view).render(CounterState.Counter(1))
    }
}
