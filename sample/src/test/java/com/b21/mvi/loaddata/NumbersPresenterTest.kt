package com.b21.mvi.loaddata

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

class NumbersPresenterTest {
    private val userIntentsRelay: Relay<UserIntent> = PublishRelay.create()
    private val view: NumbersView = mock {
        on { userIntents } doReturn userIntentsRelay
    }
    private val feature: NumbersFeature = mock()
    private val owner: LifecycleOwner = mock()
    private lateinit var stateStream: Subscriber<NumbersState>

    private val presenter = NumbersPresenter(view, feature)

    @After
    fun tearDown() {
        verifyNoMoreInteractions(view, feature, owner)
    }

    @Before
    fun launchPresenter() {
        presenter.onCreate(owner)

        argumentCaptor<Subscriber<NumbersState>>().apply {
            verify(feature).subscribeActual(capture())
            stateStream = firstValue
        }

        verify(view).userIntents
    }

    @Test
    fun testRetry() {
        userIntentsRelay.accept(UserIntent.Refresh)

        verify(feature).accept(NumbersWish.Refresh)
    }

    @Test
    fun testRender() {
        stateStream.onNext(NumbersState.Data(listOf(1L)))

        verify(view).render(NumbersState.Data(listOf(1L)))
    }
}
